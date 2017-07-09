package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.senatedb.core.model.parsing.PollingPlace.Location.{Multiple, Premises, PremisesMissingLatLong}
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint.{Absentee, Postal, PrePoll, Provisional}
import au.id.tmm.senatedb.core.model.parsing.{Division, PollingPlace, VoteCollectionPoint}
import au.id.tmm.utilities.geo.LatLong
import au.id.tmm.utilities.geo.australia.{Address, State}
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
                                                electionDao: ElectionDao,
                                                divisionDao: DivisionDao,
                                                dbStructureCache: DbStructureCache,
                                                postcodeFlyweight: PostcodeFlyweight)
                                               (implicit ec: ExecutionContext) extends VoteCollectionPointDao {

  override def write(voteCollectionPoints: Iterable[VoteCollectionPoint]): Future[Unit] = Future {

    val addressesToWrite = voteCollectionPoints.toStream
      .flatMap(VoteCollectionPoint.addressOf)
      .toSet

    DB.localTx { implicit session =>
      val addressIds: Map[Address, Long] = addressDao.writeInSession(addressesToWrite)

      val voteCollectionPointRowsToWrite = voteCollectionPoints
        .map(VoteCollectionPointRowConversions.toRow(addressIds, divisionDao, electionDao))
        .toSeq

      val statement = sql"""
           |INSERT INTO vote_collection_point(
           |  election,
           |  state,
           |  division,
           |  type,
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
           |  CAST({type} AS vote_collection_point_type),
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

  override def idPerVoteCollectionPointInSession(election: SenateElection)(implicit session: DBSession): Map[VoteCollectionPoint, Long] = {
    val electionId = electionDao.idOf(election)

    val * = dbStructureCache.columnListFor("vote_collection_point", "address", "division")

    sql"""SELECT ${*}
         |
         |  FROM vote_collection_point
         |    LEFT JOIN address ON vote_collection_point.address = address.id
         |    LEFT JOIN division ON vote_collection_point.division = division.id
         |  WHERE vote_collection_point.election = $electionId
      """.stripMargin
      .map(row => VoteCollectionPointRowConversions.fromRow(electionDao, postcodeFlyweight, alias="vote_collection_point")(row) -> row.long("vote_collection_point.id"))
      .list()
      .apply()
      .toMap
  }
}

private[daos] object VoteCollectionPointRowConversions extends RowConversions {

  val allBindingNames: Set[Symbol] = Set(
    Symbol("election"),
    Symbol("state"),
    Symbol("division"),
    Symbol("type"),
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

  def fromRow(electionDao: ElectionDao,
              postcodeFlyweight: PostcodeFlyweight,
              alias: String = "",
              addressAlias: String = "address",
              divisionAlias: String = "division")
             (row: WrappedResultSet): VoteCollectionPoint = {
    val c = aliasedColumnName(alias)(_)

    val election = electionDao.electionWithId(row.string(c("election"))).get
    val state = State.fromAbbreviation(row.string(c("state"))).get
    val division = DivisionRowConversions.fromRow(electionDao, divisionAlias)(row)

    val vcpType = row.string(c("type"))

    vcpType match {
      case "absentee" => VoteCollectionPoint.Absentee(election, state, division, row.int(c("number")))
      case "postal" => VoteCollectionPoint.Postal(election, state, division, row.int(c("number")))
      case "prepoll" => VoteCollectionPoint.PrePoll(election, state, division, row.int(c("number")))
      case "provisional" => VoteCollectionPoint.Provisional(election, state, division, row.int(c("number")))
      case "polling_place" => {
        PollingPlace(
          election,
          state,
          division,
          row.int(c("aec_id")),
          pollingPlaceTypeFrom(row.string(c("polling_place_type"))),
          row.string(c("name")),
          locationFrom(alias, addressAlias, postcodeFlyweight)(row)
        )
      }
    }
  }

  private def pollingPlaceTypeFrom(sqlTypeString: String): PollingPlace.Type.Type = {
    sqlTypeString match {
      case "polling_place" => PollingPlace.Type.POLLING_PLACE
      case "special_hospital_team" => PollingPlace.Type.SPECIAL_HOSPITAL_TEAM
      case "remote_mobile_team" => PollingPlace.Type.REMOTE_MOBILE_TEAM
      case "other_mobile_team" => PollingPlace.Type.OTHER_MOBILE_TEAM
      case "pre_poll_voting_centre" => PollingPlace.Type.PRE_POLL_VOTING_CENTRE
    }
  }

  private def locationFrom(alias: String, addressAlias: String, postcodeFlyweight: PostcodeFlyweight)
                          (row: WrappedResultSet): PollingPlace.Location = {
    val c = aliasedColumnName(alias)(_)

    val hasMultipleLocations = row.boolean(c("multiple_locations"))

    val latLong = for {
      lat <- Option(row.nullableDouble(c("latitude")))
      long <- Option(row.nullableDouble(c("longitude")))
    } yield LatLong(lat, long)

    if (hasMultipleLocations) {
      PollingPlace.Location.Multiple
    } else {
      val address = AddressRowConversions.fromRow(postcodeFlyweight, addressAlias)(row)
      val premisesName = row.string(c("premises_name"))

      latLong
        .map(PollingPlace.Location.Premises(premisesName, address, _))
        .getOrElse(PollingPlace.Location.PremisesMissingLatLong(premisesName, address))
    }
  }

  def toRow(
      addressIdLookup: Map[Address, Long],
      divisionDao: DivisionDao,
      electionDao: ElectionDao)
      (voteCollectionPoint: VoteCollectionPoint): Seq[(Symbol, Any)] = {
    val bindings = voteCollectionPointRowComponentOf(divisionDao.idOf, electionDao)(voteCollectionPoint) ++
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

  private def voteCollectionPointRowComponentOf(divisionIdLookup: Division => Long, electionDao: ElectionDao)
                                               (voteCollectionPoint: VoteCollectionPoint): Seq[(Symbol, Any)] = {
    Seq(
      Symbol("election") -> electionDao.idOf(voteCollectionPoint.election),
      Symbol("state") -> voteCollectionPoint.state.abbreviation,
      Symbol("division") -> divisionIdLookup(voteCollectionPoint.division),
      Symbol("type") -> sqlVcpTypeOf(voteCollectionPoint),
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
      case _: Absentee => "absentee"
      case _: Postal => "postal"
      case _: PrePoll => "prepoll"
      case _: Provisional => "provisional"
      case _: PollingPlace => "polling_place"
    }
  }
}