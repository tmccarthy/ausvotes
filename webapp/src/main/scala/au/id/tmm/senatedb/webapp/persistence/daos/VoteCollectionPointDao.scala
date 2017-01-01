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
        .map(VoteCollectionPointRowConversions.toRow(divisionIds, addressIds, electionDao))
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

  override def hasAnyNonPollingPlaceVoteCollectionPointsFor(election: SenateElection): Future[Boolean] = Future {
    DB.readOnly { implicit session =>
      sql"""SELECT *
           |  FROM vote_collection_point
           |  WHERE type <> 'polling_place'
           |  LIMIT 1
         """.stripMargin
        .map(_ => Unit)
        .first()
        .apply()
        .isDefined
    }
  }

  override def hasAnyPollingPlacesFor(election: SenateElection): Future[Boolean] = Future {
    DB.readOnly { implicit session =>
      sql"""SELECT *
         |  FROM vote_collection_point
         |  WHERE type = 'polling_place'
         |  LIMIT 1
         """.stripMargin
        .map(_ => Unit)
        .first()
        .apply()
        .isDefined
    }
  }
}

private[daos] object VoteCollectionPointRowConversions extends RowConversions {

  protected def fromRow(c: (String) => String, row: WrappedResultSet): VoteCollectionPoint = ???

  def toRow(divisionIdLookup: Map[Division, Long],
            addressIdLookup: Map[Address, Long],
            electionDao: ElectionDao)
           (voteCollectionPoint: VoteCollectionPoint): Seq[(Symbol, Any)] = {
    voteCollectionPointRowComponentOf(divisionIdLookup, electionDao)(voteCollectionPoint) ++
      pollingPlaceRowComponentOf(voteCollectionPoint) ++
      locationRowComponentOf(addressIdLookup)(voteCollectionPoint)
  }

  private def voteCollectionPointRowComponentOf(divisionIdLookup: Map[Division, Long], electionDao: ElectionDao)
                                               (voteCollectionPoint: VoteCollectionPoint): Seq[(Symbol, Any)] = {
    Seq(
      Symbol("election") -> electionDao.idOfBlocking(voteCollectionPoint.election),
      Symbol("state") -> voteCollectionPoint.state.abbreviation,
      Symbol("division_id") -> divisionIdLookup(voteCollectionPoint.division),
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

  private def locationRowComponentOf(addressIdLookup: Map[Address, Long])
                                    (voteCollectionPoint: VoteCollectionPoint): Seq[(Symbol, Any)] = {
    voteCollectionPoint match {
      case p: PollingPlace => p.location match {
        case Multiple => Seq(
          Symbol("multiple_locations") -> true
        )
        case Premises(name, address, location) => Seq(
          Symbol("multiple_locations") -> false,
          Symbol("premises_name") -> name,
          Symbol("address") -> addressIdLookup(address),
          Symbol("latitude") -> location.latitude,
          Symbol("longitude") -> location.longitude
        )
        case PremisesMissingLatLong(name, address) => Seq(
          Symbol("multiple_locations") -> false,
          Symbol("premises_name") -> name,
          Symbol("address") -> addressIdLookup(address)
        )
      }
      case _ => Nil
    }
  }

  private def sqlPollingPlaceTypeOf(voteCollectionPoint: VoteCollectionPoint) = {
    voteCollectionPoint match {
      case a: Absentee => "absentee"
      case p: Postal => "postal"
      case p: PrePoll => "prepoll"
      case p: Provisional => "provisional"
      case p: PollingPlace => "polling_place"
    }
  }
}