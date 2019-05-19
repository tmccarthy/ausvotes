package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.ballots

import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.FetchSenateBallots
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.FetchRawFormalSenatePreferences
import au.id.tmm.ausvotes.model.federal.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallot, SenateElectionForState, SenateGroupsAndCandidates}
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME._
import fs2.Stream

class FetchSenateBallotsFromRaw[F[+_, +_] : BME : FetchRawFormalSenatePreferences] extends FetchSenateBallots[F] {

  def senateBallotsFor(
                        election: SenateElectionForState,
                        allGroupsAndCandidates: SenateGroupsAndCandidates,
                        divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                      ): F[FetchSenateBallots.Error, Stream[F[Throwable, +?], SenateBallot]] = {

    val rawPreferenceParser = RawPreferenceParser(election, allGroupsAndCandidates)

    for {
      ballotRowStream <- implicitly[FetchRawFormalSenatePreferences[F]].formalSenatePreferencesFor(election)
          .leftMap(FetchSenateBallots.Error)

      ballots = ballotRowStream.map { row =>
        BallotGeneration.fromFormalPreferencesRow(
          election,
          rawPreferenceParser,
          divisionsAndPollingPlaces.lookupDivisionByName,
          { case (state, pollingPlaceName) => divisionsAndPollingPlaces.lookupPollingPlaceByName((state, pollingPlaceName)) },
          row,
        )
      }
    } yield ballots
  }

}

object FetchSenateBallotsFromRaw {

  def apply[F[+_, +_] : BME : FetchRawFormalSenatePreferences]: FetchSenateBallotsFromRaw[F] =
    new FetchSenateBallotsFromRaw[F]

}
