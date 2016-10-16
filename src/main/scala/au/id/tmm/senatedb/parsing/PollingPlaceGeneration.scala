package au.id.tmm.senatedb.parsing

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.flyweights.{DivisionFlyweight, PostcodeFlyweight}
import au.id.tmm.senatedb.model.parsing.PollingPlace
import au.id.tmm.senatedb.rawdata.model.PollingPlacesRow
import au.id.tmm.utilities.geo.LatLong
import au.id.tmm.utilities.geo.australia.Address
import org.apache.commons.lang3.StringUtils

object PollingPlaceGeneration {

  def fromPollingPlaceRow(election: SenateElection,
                          row: PollingPlacesRow,
                          divisionFlyweight: DivisionFlyweight = DivisionFlyweight(),
                          postcodeFlyweight: PostcodeFlyweight = PostcodeFlyweight()): PollingPlace = {
    val state = GenerationUtils.stateFrom(row.state, row)

    val pollingPlaceType = PollingPlace.Type(row.pollingPlaceTypeId)

    val location = locationFrom(row, postcodeFlyweight)

    PollingPlace(
      election,
      state,
      divisionFlyweight(election, state, row.divisionName, row.divisionId),
      row.pollingPlaceId,
      pollingPlaceType,
      row.pollingPlaceName,
      location
    )
  }

  private def locationFrom(row: PollingPlacesRow, postcodeFlyweight: PostcodeFlyweight): PollingPlace.Location = {
    row.premisesName.trim match {
      case "Multiple sites" => PollingPlace.Location.Multiple
      case premisesName => {
        val address = premisesAddressFrom(row, postcodeFlyweight)
        val possibleLatLong = latLongFrom(row)

        possibleLatLong match {
          case Some(latLong) => PollingPlace.Location.Premises(premisesName, address, latLong)
          case None => PollingPlace.Location.PremisesMissingLatLong(premisesName, address)
        }
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

  private def premisesAddressFrom(row: PollingPlacesRow, postcodeFlyweight: PostcodeFlyweight): Address = {
    Address(
      lines = addressLinesFrom(row),
      suburb = row.premisesSuburb.trim,
      postcode = postcodeFlyweight(row.premisesPostcode.trim),
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
