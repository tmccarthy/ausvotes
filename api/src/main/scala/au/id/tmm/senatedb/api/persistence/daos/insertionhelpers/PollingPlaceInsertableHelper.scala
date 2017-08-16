package au.id.tmm.senatedb.api.persistence.daos.insertionhelpers

import au.id.tmm.senatedb.api.persistence.daos.ElectionDao
import au.id.tmm.senatedb.api.persistence.daos.insertionhelpers.InsertableSupport.Insertable
import au.id.tmm.senatedb.core.model.parsing.PollingPlace
import au.id.tmm.senatedb.core.model.parsing.PollingPlace.Location.{Multiple, Premises, PremisesMissingLatLong}
import au.id.tmm.senatedb.core.model.parsing.PollingPlace.PollingPlaceType
import au.id.tmm.utilities.geo.australia.Address
import au.id.tmm.utilities.hashing.Pairing

private[daos] object PollingPlaceInsertableHelper {

  def idOf(pollingPlace: PollingPlace): Long = {
    val electionCode = pollingPlace.election.aecID

    val pollingPlaceCode = pollingPlace.aecId

    Pairing.Szudzik.pair(electionCode, pollingPlaceCode)
  }

  def toInsertable(addressIdLookup: Address => Long)(pollingPlace: PollingPlace): Insertable = {

    val locationFields: Insertable = {
      pollingPlace.location match {
        case Multiple => Seq(
          'multiple_locations -> true,
          'premises_name -> null,
          'address -> null,
          'latitude -> null,
          'longitude -> null,
        )
        case Premises(name, address, location) => Seq(
          'multiple_locations -> false,
          'premises_name -> name,
          'address -> addressIdLookup(address),
          'latitude -> location.latitude,
          'longitude -> location.longitude
        )
        case PremisesMissingLatLong(name, address) => Seq(
          'multiple_locations -> false,
          'premises_name -> name,
          'address -> addressIdLookup(address),
          'latitude -> null,
          'longitude -> null,
        )
      }
    }

    val mainFields: Insertable = Seq(
      'id -> idOf(pollingPlace),
      'election -> ElectionDao.idOf(pollingPlace.election).get,
      'state -> pollingPlace.state.abbreviation,
      'division -> DivisionInsertableHelper.idOf(pollingPlace.division),
      'aec_id -> pollingPlace.aecId,
      'polling_place_type -> pollingPlaceTypeToString(pollingPlace.pollingPlaceType),
      'name -> pollingPlace.name,
    )

    mainFields ++ locationFields
  }

  def pollingPlaceTypeToString(pollingPlaceType: PollingPlaceType): String = {
    pollingPlaceType match {
      case PollingPlaceType.PollingPlace => "polling_place"
      case PollingPlaceType.SpecialHospitalTeam => "special_hospital_team"
      case PollingPlaceType.RemoteMobileTeam => "remote_mobile_team"
      case PollingPlaceType.OtherMobileTeam => "other_mobile_team"
      case PollingPlaceType.PrePollVotingCentre => "pre_poll_voting_centre"
    }
  }

}
