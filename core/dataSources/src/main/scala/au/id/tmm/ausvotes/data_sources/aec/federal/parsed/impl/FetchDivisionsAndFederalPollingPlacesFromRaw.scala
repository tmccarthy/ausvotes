package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl

import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.FetchDivisionsAndFederalPollingPlaces
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.CommonParsing._
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.FetchDivisionsAndFederalPollingPlacesFromRaw.DivisionAndPollingPlace
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.FetchRawFederalPollingPlaces
import au.id.tmm.ausvotes.data_sources.common.Fs2Interop._
import au.id.tmm.ausvotes.model.Electorate
import au.id.tmm.ausvotes.model.federal.FederalVoteCollectionPoint.FederalPollingPlace
import au.id.tmm.ausvotes.model.federal.FederalVoteCollectionPoint.FederalPollingPlace.PollingPlaceType
import au.id.tmm.ausvotes.model.federal.{Division, DivisionsAndPollingPlaces, FederalElection}
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME._
import au.id.tmm.bfect.effects.Sync
import au.id.tmm.utilities.collection.Flyweight
import au.id.tmm.utilities.geo.LatLong
import au.id.tmm.utilities.geo.australia.{Address, Postcode, State}
import org.apache.commons.lang3.StringUtils

final class FetchDivisionsAndFederalPollingPlacesFromRaw[F[+_, +_] : FetchRawFederalPollingPlaces : Sync] private() extends FetchDivisionsAndFederalPollingPlaces[F] {

  private val divisionFlyweight: Flyweight[(FederalElection, State, String, Electorate.Id), Division] = Flyweight { tuple =>
    Electorate(tuple._1, tuple._2, tuple._3, tuple._4)
  }

  override def divisionsAndFederalPollingPlacesFor(
                                                    election: FederalElection,
                                                  ): F[FetchDivisionsAndFederalPollingPlaces.Error, DivisionsAndPollingPlaces] = {
    for {
      pollingPlacesRows <- implicitly[FetchRawFederalPollingPlaces[F]].federalPollingPlacesForElection(election)
        .leftMap(FetchDivisionsAndFederalPollingPlaces.Error)

      divisionsAndPollingPlacesStream = pollingPlacesRows.evalMap(row => BME.fromEither(parseRawRow(election, row)): F[Throwable, DivisionAndPollingPlace])

      divisionsAndPollingPlaces <- divisionsAndPollingPlacesStream.compile.toVector
        .swallowThrowablesAndWrapIn(FetchDivisionsAndFederalPollingPlaces.Error)
    } yield {
      val divisionsBuilder = Set.newBuilder[Division]
      val pollingPlacesBuilder = Set.newBuilder[FederalPollingPlace]

      divisionsAndPollingPlaces.foreach { case DivisionAndPollingPlace(division, pollingPlace) =>
        divisionsBuilder += division
        pollingPlacesBuilder += pollingPlace
      }

      DivisionsAndPollingPlaces(
        divisionsBuilder.result(),
        pollingPlacesBuilder.result(),
      )
    }
  }

  private[impl] def parseRawRow(election: FederalElection, row: FetchRawFederalPollingPlaces.Row): Either[FetchDivisionsAndFederalPollingPlaces.Error, DivisionAndPollingPlace] = {

    for {
      state <- parseState(row.state)
        .left.map(FetchDivisionsAndFederalPollingPlaces.Error)

      location <- locationFrom(row)
    } yield {
      val pollingPlaceType = row.pollingPlaceTypeId match {
        case 1 => PollingPlaceType.PollingPlace
        case 2 => PollingPlaceType.SpecialHospitalTeam
        case 3 => PollingPlaceType.RemoteMobileTeam
        case 4 => PollingPlaceType.OtherMobileTeam
        case 5 => PollingPlaceType.PrePollVotingCentre
      }

      val division = divisionFlyweight((election, state, row.divisionName, Electorate.Id(row.divisionId)))

      val pollingPlace = FederalPollingPlace(
        election,
        state,
        division,
        FederalPollingPlace.Id(row.pollingPlaceId),
        pollingPlaceType,
        row.pollingPlaceName,
        location,
      )

      DivisionAndPollingPlace(division, pollingPlace)
    }
  }

  private def locationFrom(row: FetchRawFederalPollingPlaces.Row): Either[FetchDivisionsAndFederalPollingPlaces.Error, FederalPollingPlace.Location] = {
    row.premisesName.trim match {
      case "Multiple sites" => Right(FederalPollingPlace.Location.Multiple)
      case premisesName =>
        premisesAddressFrom(row).map { address =>
          val possibleLatLong = latLongFrom(row)

          FederalPollingPlace.Location.Premises(premisesName, address, possibleLatLong)
        }
    }
  }

  private def latLongFrom(row: FetchRawFederalPollingPlaces.Row): Option[LatLong] = {
    for {
      lat <- row.latitude
      long <- row.longitude
      latLong <- Some(LatLong(lat, long))
    } yield latLong
  }

  private def premisesAddressFrom(row: FetchRawFederalPollingPlaces.Row): Either[FetchDivisionsAndFederalPollingPlaces.Error, Address] =
    parseState(row.premisesState).map { state =>
      Address(
        lines = addressLinesFrom(row),
        suburb = row.premisesSuburb.trim,
        postcode = Postcode(row.premisesPostcode.trim),
        state = state,
      )
    }.left.map(FetchDivisionsAndFederalPollingPlaces.Error)

  private def addressLinesFrom(row: FetchRawFederalPollingPlaces.Row): Vector[String] = {
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

object FetchDivisionsAndFederalPollingPlacesFromRaw {

  def apply[F[+_, +_] : FetchRawFederalPollingPlaces : Sync]: FetchDivisionsAndFederalPollingPlacesFromRaw[F] = new FetchDivisionsAndFederalPollingPlacesFromRaw()

  private[impl] final case class DivisionAndPollingPlace(division: Division, pollingPlace: FederalPollingPlace)

}
