package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.persistence.daos.rowentities.{AddressRow, DivisionRow, VoteCollectionPointRow}
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.senatedb.core.model.parsing.PollingPlace.Location.{Multiple, Premises, PremisesMissingLatLong}
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint.{Absentee, Postal, PrePoll, Provisional}
import au.id.tmm.senatedb.core.model.parsing.{Division, PollingPlace, VoteCollectionPoint}
import au.id.tmm.utilities.geo.australia.Address
import com.google.common.base.CaseFormat
import com.google.inject.{ImplementedBy, Inject, Singleton}
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ConcreteVoteCollectionPointDao])
trait VoteCollectionPointDao {
  def write(voteCollectionPoints: Iterable[VoteCollectionPoint]): Future[Unit]

  def hasAnyNonPollingPlaceVoteCollectionPointsFor(election: SenateElection): Future[Boolean]

  def hasAnyPollingPlacesFor(election: SenateElection): Future[Boolean]

  def idPerVoteCollectionPointInSession(election: SenateElection)(implicit session: DBSession): Map[VoteCollectionPoint, Long]
}

@Singleton
class ConcreteVoteCollectionPointDao @Inject() (addressDao: AddressDao,
                                                divisionDao: DivisionDao,
                                                postcodeFlyweight: PostcodeFlyweight)
                                               (implicit ec: ExecutionContext) extends VoteCollectionPointDao {

  override def write(voteCollectionPoints: Iterable[VoteCollectionPoint]): Future[Unit] = Future {

    val addressesToWrite = voteCollectionPoints.toStream
      .flatMap(VoteCollectionPoint.addressOf)
      .toSet

    DB.localTx { implicit session =>
      val addressIds: Map[Address, Long] = addressDao.writeInSession(addressesToWrite)

      val voteCollectionPointRowsToWrite = voteCollectionPoints
        .map(VoteCollectionPointRowConversions.toRow(addressIds, divisionDao))
        .toSeq

      val statement = sql"""
           |INSERT INTO vote_collection_point(
           |  election,
           |  state,
           |  division,
           |  vote_collection_point_type,
           |  name,
           |  number,
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
           |  {division},
           |  CAST({vote_collection_point_type} AS vote_collection_point_type),
           |  {name},
           |  {number},
           |  {aec_id},
           |  CAST({polling_place_type} AS polling_place_type),
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

  override def hasAnyNonPollingPlaceVoteCollectionPointsFor(election: SenateElection): Future[Boolean] = Future {
    DB.localTx { implicit session =>

      val v = VoteCollectionPointRow.syntax

      withSQL {
        select(v.id)
          .from(VoteCollectionPointRow as v)
          .where(sqls"CAST(${v.voteCollectionPointType} AS VARCHAR) <> ${"polling_place"}")
          .limit(1)
      }
        .map(_.long(1))
        .first()
        .apply()
        .isDefined
    }
  }

  override def hasAnyPollingPlacesFor(election: SenateElection): Future[Boolean] = Future {
    DB.localTx { implicit session =>

      val v = VoteCollectionPointRow.syntax

      withSQL {
        select(v.id)
          .from(VoteCollectionPointRow as v)
          .where(sqls"CAST(${v.voteCollectionPointType} AS VARCHAR) = ${"polling_place"}")
          .limit(1)
      }
        .map(_.long(1))
        .first()
        .apply()
        .isDefined
    }
  }

  override def idPerVoteCollectionPointInSession(election: SenateElection)(implicit session: DBSession): Map[VoteCollectionPoint, Long] = {
    val electionId = ElectionDao.idOf(election)

    val (v, d, a) = (VoteCollectionPointRow.syntax, DivisionRow.syntax, AddressRow.syntax)

    withSQL {
      select
        .from(VoteCollectionPointRow as v)
        .leftJoin(DivisionRow as d).on(v.division, d.id)
        .leftJoin(AddressRow as a).on(v.address, a.id)
        .where.eq(v.election, electionId)
    }
      .map(VoteCollectionPointRow(postcodeFlyweight, v, d, a))
      .list()
      .apply()
      .toStream
      .map(vcpRow => vcpRow.asVoteCollectionPoint -> vcpRow.id)
      .toMap
  }
}

private[daos] object VoteCollectionPointRowConversions extends RowConversions {

  val allBindingNames: Set[Symbol] = Set(
    Symbol("election"),
    Symbol("state"),
    Symbol("division"),
    Symbol("vote_collection_point_type"),
    Symbol("name"),
    Symbol("number"),
    Symbol("aec_id"),
    Symbol("polling_place_type"),
    Symbol("multiple_locations"),
    Symbol("premises_name"),
    Symbol("address"),
    Symbol("latitude"),
    Symbol("longitude")
  )

  def toRow(
      addressIdLookup: Map[Address, Long],
      divisionDao: DivisionDao,
      )(voteCollectionPoint: VoteCollectionPoint): Seq[(Symbol, Any)] = {
    val bindings = voteCollectionPointRowComponentOf(divisionDao.idOf)(voteCollectionPoint) ++
      numberComponentOf(voteCollectionPoint) ++
      pollingPlaceRowComponentOf(voteCollectionPoint) ++
      locationRowComponentOf(addressIdLookup)(voteCollectionPoint)

    fillInMissingBindings(bindings)
  }

  private def fillInMissingBindings(bindings: Seq[(Symbol, Any)]): Seq[(Symbol, Any)] = {
    val usedBindingsNames = bindings.toStream
      .map {
        case (bindingName, value) => bindingName
      }
      .toSet

    val missingBindingNames = allBindingNames diff usedBindingsNames

    val missingBindings = missingBindingNames.toStream
      .map(bindingName => bindingName -> null)

    bindings ++ missingBindings
  }

  private def voteCollectionPointRowComponentOf(divisionIdLookup: Division => Long)
                                               (voteCollectionPoint: VoteCollectionPoint): Seq[(Symbol, Any)] = {
    Seq(
      Symbol("election") -> ElectionDao.idOf(voteCollectionPoint.election),
      Symbol("state") -> voteCollectionPoint.state.abbreviation,
      Symbol("division") -> divisionIdLookup(voteCollectionPoint.division),
      Symbol("vote_collection_point_type") -> sqlVcpTypeOf(voteCollectionPoint),
      Symbol("name") -> voteCollectionPoint.name
    )
  }

  private def sqlVcpTypeOf(voteCollectionPoint: VoteCollectionPoint): String = voteCollectionPoint match {
    case _: Absentee => "absentee"
    case _: Postal => "postal"
    case _: PrePoll => "prepoll"
    case _: Provisional => "provisional"
    case _: PollingPlace => "polling_place"
  }

  private def numberComponentOf(voteCollectionPoint: VoteCollectionPoint): Seq[(Symbol, Any)] = {
    val number = voteCollectionPoint match {
      case a: Absentee => Some(a.number)
      case p: Postal => Some(p.number)
      case p: PrePoll => Some(p.number)
      case p: Provisional => Some(p.number)
      case p: PollingPlace => None
    }

    number.map(n => Symbol("number") -> n).toSeq
  }

  private def pollingPlaceRowComponentOf(voteCollectionPoint: VoteCollectionPoint): Seq[(Symbol, Any)] = {
    voteCollectionPoint match {
      case p: PollingPlace => Seq(
        Symbol("aec_id") -> p.aecId,
        Symbol("polling_place_type") -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, p.pollingPlaceType.toString)
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
      case _: Absentee => "absentee"
      case _: Postal => "postal"
      case _: PrePoll => "prepoll"
      case _: Provisional => "provisional"
      case _: PollingPlace => "polling_place"
    }
  }
}