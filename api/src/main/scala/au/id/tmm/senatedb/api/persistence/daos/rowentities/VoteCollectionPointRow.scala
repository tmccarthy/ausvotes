package au.id.tmm.senatedb.api.persistence.daos.rowentities

import au.id.tmm.senatedb.api.persistence.daos.ElectionDao
import au.id.tmm.senatedb.api.persistence.daos.rowentities.VoteCollectionPointRow.VcpType
import au.id.tmm.senatedb.core.model
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.senatedb.core.model.parsing.PollingPlace.{Location, PollingPlaceType}
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint
import au.id.tmm.utilities.geo.LatLong
import au.id.tmm.utilities.geo.australia.State
import scalikejdbc._

private[daos] final case class VoteCollectionPointRow(
                                                       id: Long,
                                                       election: SenateElection,
                                                       state: State,
                                                       division: DivisionRow,

                                                       voteCollectionPointType: VcpType,
                                                       name: String,

                                                       number: Option[Int],

                                                       aecId: Option[Int],
                                                       pollingPlaceType: Option[PollingPlaceType],
                                                       multipleLocations: Option[Boolean],

                                                       premisesName: Option[String],
                                                       address: Option[AddressRow],

                                                       latitude: Option[Double],
                                                       longitude: Option[Double],
                                                     ) {
  def asVoteCollectionPoint: VoteCollectionPoint = {
    voteCollectionPointType match {
      case VcpType.Absentee => VoteCollectionPoint.Absentee(election, state, division.asDivision, number.get)
      case VcpType.Postal => VoteCollectionPoint.Postal(election, state, division.asDivision, number.get)
      case VcpType.Prepoll => VoteCollectionPoint.PrePoll(election, state, division.asDivision, number.get)
      case VcpType.Provisional => VoteCollectionPoint.Provisional(election, state, division.asDivision, number.get)
      case VcpType.PollingPlace => model.parsing.PollingPlace(
        election,
        state,
        division.asDivision,
        aecId.get,
        pollingPlaceType.get,
        name,
        location = location.get
      )
    }
  }

  def location: Option[Location] = {
    multipleLocations match {
      case None => None
      case Some(true) => Some(Location.Multiple)
      case Some(false) => {
        latLong match {
          case Some(latLong) => Some(Location.Premises(premisesName.get, address.get.asAddress, latLong))
          case None => Some(Location.PremisesMissingLatLong(premisesName.get, address.get.asAddress))
        }
      }
    }
  }

  def latLong: Option[LatLong] = {
    for {
      presentLat <- latitude
      presentLong <- longitude
    } yield LatLong(presentLat, presentLong)
  }
}

private[daos] object VoteCollectionPointRow extends SQLSyntaxSupport[VoteCollectionPointRow] {

  override def tableName: String = "vote_collection_point"

  def apply(postcodeFlyweight: PostcodeFlyweight,
            v: SyntaxProvider[VoteCollectionPointRow],
            d: SyntaxProvider[DivisionRow],
            a: SyntaxProvider[AddressRow],
           )(rs: WrappedResultSet): VoteCollectionPointRow =
    apply(postcodeFlyweight, v.resultName, d.resultName, a.resultName)(rs)

  def apply(postcodeFlyweight: PostcodeFlyweight,
            v: ResultName[VoteCollectionPointRow],
            d: ResultName[DivisionRow],
            a: ResultName[AddressRow],
           )(rs: WrappedResultSet): VoteCollectionPointRow = {
    VoteCollectionPointRow(
      id = rs.long(v.id),
      election = ElectionDao.electionWithId(rs.string(v.election)).get,
      state = State.fromAbbreviation(rs.string(v.state)).get,
      division = DivisionRow(d)(rs),
      parseVcpType(rs.string(v.voteCollectionPointType)),
      name = rs.string(v.name),
      number = rs.intOpt(v.number),
      aecId = rs.intOpt(v.aecId),
      pollingPlaceType = rs.stringOpt(v.pollingPlaceType).map(parsePollingPlaceType),
      multipleLocations = rs.booleanOpt(v.multipleLocations),
      premisesName = rs.stringOpt(v.premisesName),
      address = AddressRow.opt(postcodeFlyweight, a)(rs),
      latitude = rs.doubleOpt(v.latitude),
      longitude = rs.doubleOpt(v.longitude),
    )
  }

  private def parseVcpType(asString: String): VcpType = {
    asString match {
      case "absentee" => VcpType.Absentee
      case "postal" => VcpType.Postal
      case "prepoll" => VcpType.Prepoll
      case "provisional" => VcpType.Provisional
      case "polling_place" => VcpType.PollingPlace
    }
  }

  private def parsePollingPlaceType(asString: String): PollingPlaceType = {
    asString match {
      case "polling_place" => PollingPlaceType.PollingPlace
      case "special_hospital_team" => PollingPlaceType.SpecialHospitalTeam
      case "remote_mobile_team" => PollingPlaceType.RemoteMobileTeam
      case "other_mobile_team" => PollingPlaceType.OtherMobileTeam
      case "pre_poll_voting_centre" => PollingPlaceType.PrePollVotingCentre
    }
  }

  sealed trait VcpType

  object VcpType {
    case object Absentee extends VcpType
    case object Postal extends VcpType
    case object Prepoll extends VcpType
    case object Provisional extends VcpType
    case object PollingPlace extends VcpType
  }

  def opt(postcodeFlyweight: PostcodeFlyweight,
          v: ResultName[VoteCollectionPointRow],
          d: ResultName[DivisionRow],
          a: ResultName[AddressRow],
         )(rs: WrappedResultSet): Option[VoteCollectionPointRow] = {
    rs.longOpt(v.id).map(_ => VoteCollectionPointRow(postcodeFlyweight, v, d, a)(rs))
  }
}