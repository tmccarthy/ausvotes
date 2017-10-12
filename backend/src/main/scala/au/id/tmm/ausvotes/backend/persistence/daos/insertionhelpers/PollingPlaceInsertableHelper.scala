package au.id.tmm.ausvotes.backend.persistence.daos.insertionhelpers

import au.id.tmm.ausvotes.backend.persistence.daos.enumconverters.{ElectionEnumConverter, PollingPlaceTypeEnumConverter, StateEnumConverter}
import au.id.tmm.ausvotes.backend.persistence.daos.insertionhelpers.InsertableSupport.Insertable
import au.id.tmm.ausvotes.core.model.parsing.PollingPlace
import au.id.tmm.ausvotes.core.model.parsing.PollingPlace.Location.{Multiple, Premises, PremisesMissingLatLong}
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
      'election -> ElectionEnumConverter(pollingPlace.election),
      'state -> StateEnumConverter(pollingPlace.state),
      'division -> DivisionInsertableHelper.idOf(pollingPlace.division),
      'aec_id -> pollingPlace.aecId,
      'polling_place_type -> PollingPlaceTypeEnumConverter(pollingPlace.pollingPlaceType),
      'name -> pollingPlace.name,
    )

    mainFields ++ locationFields
  }
}
