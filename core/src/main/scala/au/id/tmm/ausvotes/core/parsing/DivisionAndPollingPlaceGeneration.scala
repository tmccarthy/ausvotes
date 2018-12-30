package au.id.tmm.ausvotes.core.parsing

import au.id.tmm.ausvotes.core.model.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.core.model.DivisionsAndPollingPlaces.DivisionAndPollingPlace
import au.id.tmm.ausvotes.core.rawdata.model.PollingPlacesRow
import au.id.tmm.ausvotes.model.Electorate
import au.id.tmm.ausvotes.model.Flyweights.ElectorateFlyweight
import au.id.tmm.ausvotes.model.VoteCollectionPoint.PollingPlace
import au.id.tmm.ausvotes.model.VoteCollectionPoint.PollingPlace.PollingPlaceType
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.utilities.geo.LatLong
import au.id.tmm.utilities.geo.australia.{Address, Postcode, State}
import org.apache.commons.lang3.StringUtils

object DivisionAndPollingPlaceGeneration {

  def fromPollingPlaceRows(
                            election: FederalElection,
                            rows: TraversableOnce[PollingPlacesRow],
                            electorateFlyweight: ElectorateFlyweight[FederalElection, State] = ElectorateFlyweight(),
                          ): DivisionsAndPollingPlaces = {
    val divisionsAndPollingPlaces: TraversableOnce[DivisionAndPollingPlace] = rows
      .map(row => fromPollingPlaceRow(election, row, electorateFlyweight))

    DivisionsAndPollingPlaces.from(divisionsAndPollingPlaces)
  }

  def fromPollingPlaceRow(
                           election: FederalElection,
                           row: PollingPlacesRow,
                           electorateFlyweight: ElectorateFlyweight[FederalElection, State] = ElectorateFlyweight(),
                         ): DivisionAndPollingPlace = {
    val state = GenerationUtils.stateFrom(row.state, row)

    val pollingPlaceType = row.pollingPlaceTypeId match {
      case 1 => PollingPlaceType.PollingPlace
      case 2 => PollingPlaceType.SpecialHospitalTeam
      case 3 => PollingPlaceType.RemoteMobileTeam
      case 4 => PollingPlaceType.OtherMobileTeam
      case 5 => PollingPlaceType.PrePollVotingCentre
    }

    val location = locationFrom(row)

    val division = electorateFlyweight.make(election, state, row.divisionName, Electorate.Id(row.divisionId))

    val pollingPlace = PollingPlace(
      election,
      jurisdiction = au.id.tmm.ausvotes.model.federal.FederalVcpJurisdiction(state, division),
      PollingPlace.Id(row.pollingPlaceId),
      pollingPlaceType,
      row.pollingPlaceName,
      location,
    )

    DivisionAndPollingPlace(division, pollingPlace)
  }

  private def locationFrom(row: PollingPlacesRow): PollingPlace.Location = {
    row.premisesName.trim match {
      case "Multiple sites" => PollingPlace.Location.Multiple
      case premisesName => {
        val address = premisesAddressFrom(row)
        val possibleLatLong = latLongFrom(row)

        PollingPlace.Location.Premises(premisesName, address, possibleLatLong)
      }
    }
  }

  private def latLongFrom(row: PollingPlacesRow): Option[LatLong] = {
    for {
      lat <- row.latitude
      long <- row.longitude
      latLong <- Some(LatLong(lat, long))
    } yield latLong
  }

  private def premisesAddressFrom(row: PollingPlacesRow): Address = {
    Address(
      lines = addressLinesFrom(row),
      suburb = row.premisesSuburb.trim,
      postcode = Postcode(row.premisesPostcode.trim),
      state = GenerationUtils.stateFrom(row.premisesState, row)
    )
  }

  private def addressLinesFrom(row: PollingPlacesRow): Vector[String] = {
    val addressLines = Stream(row.premisesAddress1, row.premisesAddress2, row.premisesAddress3)
      .filterNot(StringUtils.isBlank)
      .map(_.trim)
      .toVector

    if (addressLines.nonEmpty) {
      addressLines
    } else {
      Vector(row.premisesName.trim)
    }
  }
}
