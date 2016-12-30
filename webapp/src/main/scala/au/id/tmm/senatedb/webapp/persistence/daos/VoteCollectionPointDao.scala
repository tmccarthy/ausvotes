package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.PollingPlace.Location.{Multiple, Premises, PremisesMissingLatLong}
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint.{Absentee, Postal, PrePoll, Provisional}
import au.id.tmm.senatedb.core.model.parsing.{Division, PollingPlace, VoteCollectionPoint}
import au.id.tmm.utilities.geo.australia.Address
import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.libs.concurrent.Execution.Implicits._
import scalikejdbc._

import scala.concurrent.Future

@ImplementedBy(classOf[ConcreteVoteCollectionPointDao])
trait VoteCollectionPointDao {
  def write(voteCollectionPoints: Iterable[VoteCollectionPoint]): Future[Unit]

  def allAtElection(election: SenateElection): Future[Set[VoteCollectionPoint]]

  def hasAnyNonPollingPlaceVoteCollectionPointsFor(election: SenateElection): Future[Boolean]

  def hasAnyPollingPlacesFor(election: SenateElection): Future[Boolean]
}

@Singleton
class ConcreteVoteCollectionPointDao @Inject() (connectionPool: ConnectionPoolContext,
                                                addressDao: AddressDao,
                                                electionDao: ElectionDao,
                                                divisionDao: DivisionDao) extends VoteCollectionPointDao {

  override def write(voteCollectionPoints: Iterable[VoteCollectionPoint]): Future[Unit] = Future {

    val addressesToWrite = voteCollectionPoints.toStream
      .flatMap(VoteCollectionPoint.addressOf)
      .toSet

    DB.localTx { implicit session =>
      val addressIds: Map[Address, Long] = addressDao.writeInSession(addressesToWrite)
      val divisionIds: Map[Division, Long] = divisionDao.allWithIdsInSession

      val voteCollectionPointRowsToWrite = voteCollectionPoints
        .map(voteCollectionPointToRow(_, addressIds, divisionIds))
        .toSeq

      val statement = sql"""INSERT INTO vote_collection_point(
           |  election,
           |  state,
           |  division_id,
           |  type,
           |  name,
           |  aec_id,
           |  polling_place_type,
           |  multiple_locations,
           |  premises_name,
           |  address,
           |  latitude,
           |  longitude
           |) VALUES (
           |  {election},
           |  {state},
           |  {division_id},
           |  {type},
           |  {name},
           |  {aec_id},
           |  {polling_place_type},
           |  {multiple_locations},
           |  {premises_name},
           |  {address},
           |  {latitude},
           |  {longitude}
           |)""".stripMargin
        .batchByName(voteCollectionPointRowsToWrite: _*)

      statement.apply()
    }
  }

  override def allAtElection(election: SenateElection): Future[Set[VoteCollectionPoint]] = ???

  override def hasAnyNonPollingPlaceVoteCollectionPointsFor(election: SenateElection): Future[Boolean] = ???

  override def hasAnyPollingPlacesFor(election: SenateElection): Future[Boolean] = ???

  private def voteCollectionPointToRow(voteCollectionPoint: VoteCollectionPoint,
                                       addressIds: Map[Address, Long],
                                       divisionIds: Map[Division, Long]
                                      ): Seq[(Symbol, Any)] = {
    voteCollectionPointRowComponentOf(voteCollectionPoint, divisionIds) ++
      pollingPlaceRowComponentOf(voteCollectionPoint) ++
      locationRowComponentOf(voteCollectionPoint, addressIds)
  }

  private def voteCollectionPointRowComponentOf(voteCollectionPoint: VoteCollectionPoint,
                                                divisionIds: Map[Division, Long]): Seq[(Symbol, Any)] = {
    Seq(
      Symbol("election") -> electionDao.idOfBlocking(voteCollectionPoint.election),
      Symbol("state") -> voteCollectionPoint.state.abbreviation,
      Symbol("division_id") -> divisionIds(voteCollectionPoint.division),
      Symbol("type") -> sqlPollingPlaceTypeOf(voteCollectionPoint),
      Symbol("name") -> voteCollectionPoint.name
    )
  }

  private def pollingPlaceRowComponentOf(voteCollectionPoint: VoteCollectionPoint): Seq[(Symbol, Any)] = {
    voteCollectionPoint match {
      case p: PollingPlace => Seq(
        Symbol("aec_id") -> p.aecId,
        Symbol("polling_place_type") -> p.pollingPlaceType.toString.toLowerCase
      )
      case _ => Nil
    }
  }

  private def locationRowComponentOf(voteCollectionPoint: VoteCollectionPoint,
                                     addressIds: Map[Address, Long]): Seq[(Symbol, Any)] = {
    voteCollectionPoint match {
      case p: PollingPlace => p.location match {
        case Multiple => Seq(
          Symbol("multiple_locations") -> true
        )
        case Premises(name, address, location) => Seq(
          Symbol("multiple_locations") -> false,
          Symbol("premises_name") -> name,
          Symbol("address") -> addressIds(address),
          Symbol("latitude") -> location.latitude,
          Symbol("longitude") -> location.longitude
        )
        case PremisesMissingLatLong(name, address) => Seq(
          Symbol("multiple_locations") -> false,
          Symbol("premises_name") -> name,
          Symbol("address") -> addressIds(address)
        )
      }
      case _ => Nil
    }
  }

  private def sqlPollingPlaceTypeOf(voteCollectionPoint: VoteCollectionPoint) = {
    voteCollectionPoint match {
      case Absentee(election, state, division, number) => "absentee"
      case Postal(election, state, division, number) => "postal"
      case PrePoll(election, state, division, number) => "prepoll"
      case Provisional(election, state, division, number) => "provisional"
      case PollingPlace(election, state, division, aecId, pollingPlaceType, name, location) => "polling_place"
    }
  }

  private def voteCollectionPointFromRow(wrappedResultSet: WrappedResultSet): VoteCollectionPoint = ???
}