package au.id.tmm.ausvotes.api.persistence.daos.rowentities

import au.id.tmm.ausvotes.api.persistence.daos.enumconverters.{ElectionEnumConverter, PollingPlaceTypeEnumConverter, StateEnumConverter}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.ausvotes.core.model.parsing.PollingPlace
import au.id.tmm.ausvotes.core.model.parsing.PollingPlace.{Location, PollingPlaceType}
import au.id.tmm.utilities.geo.LatLong
import au.id.tmm.utilities.geo.australia.State
import scalikejdbc._

final case class PollingPlaceRow(
                                  id: Long,

                                  election: SenateElection,
                                  state: State,
                                  division: DivisionRow,

                                  name: String,
                                  aecId: Int,

                                  pollingPlaceType: PollingPlaceType,
                                  multipleLocations: Boolean,

                                  premisesName: Option[String],
                                  address: Option[AddressRow],
                                  latitude: Option[Double],
                                  longitude: Option[Double],
                                ) extends VoteCollectionPointRow {

  def latLong: Option[LatLong] = for {
    presentLat <- latitude
    presentLong <- longitude
  } yield LatLong(presentLat, presentLong)

  def location: Location = {
    if (multipleLocations) {
      Location.Multiple
    } else {
      this.latLong match {
        case None => Location.PremisesMissingLatLong(premisesName.get, address.get.asAddress)
        case Some(latLong) => Location.Premises(premisesName.get, address.get.asAddress, latLong)
      }
    }
  }

  override def asVoteCollectionPoint: PollingPlace = {
    PollingPlace(
      election,
      state,
      division.asDivision,
      aecId,
      pollingPlaceType,
      name,
      location,
    )
  }
}

object PollingPlaceRow extends SQLSyntaxSupport[PollingPlaceRow] {

  override def tableName: String = "polling_place"

  def apply(postcodeFlyweight: PostcodeFlyweight,
            p: SyntaxProvider[PollingPlaceRow],
            d: SyntaxProvider[DivisionRow],
            a: SyntaxProvider[AddressRow],
           )(rs: WrappedResultSet): PollingPlaceRow = apply(postcodeFlyweight, p.resultName, d.resultName, a.resultName)(rs)


  def apply(postcodeFlyweight: PostcodeFlyweight,
            p: ResultName[PollingPlaceRow],
            d: ResultName[DivisionRow],
            a: ResultName[AddressRow],
           )(rs: WrappedResultSet): PollingPlaceRow = {
    PollingPlaceRow(
      id = rs.long(p.id),
      election = ElectionEnumConverter(rs.string(p.election)),
      state = StateEnumConverter(rs.string(p.state)),
      division = DivisionRow(d)(rs),
      name = rs.string(p.name),
      aecId = rs.int(p.aecId),
      pollingPlaceType = PollingPlaceTypeEnumConverter(rs.string(p.pollingPlaceType)),
      multipleLocations = rs.boolean(p.multipleLocations),
      premisesName = rs.stringOpt(p.premisesName),
      address = AddressRow.opt(postcodeFlyweight, a)(rs),
      latitude = rs.doubleOpt(p.latitude),
      longitude = rs.doubleOpt(p.longitude),
    )
  }

  def opt(postcodeFlyweight: PostcodeFlyweight,
          p: ResultName[PollingPlaceRow],
          d: ResultName[DivisionRow],
          a: ResultName[AddressRow],
         )(rs: WrappedResultSet): Option[PollingPlaceRow] = {
    rs.longOpt(p.id).map(_ => PollingPlaceRow(postcodeFlyweight, p, d, a)(rs))
  }
}