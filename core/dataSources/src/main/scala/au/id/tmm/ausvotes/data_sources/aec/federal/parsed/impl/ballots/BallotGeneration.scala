package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.ballots

import au.id.tmm.ausvotes.data_sources.aec.federal.raw.FetchRawFormalSenatePreferences
import au.id.tmm.ausvotes.model.federal.FederalVoteCollectionPoint.FederalPollingPlace
import au.id.tmm.ausvotes.model.federal.FederalVoteCollectionPoint.Special.SpecialVcpType
import au.id.tmm.ausvotes.model.federal._
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallot, SenateBallotId, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State

// TODO make package-private
object BallotGeneration {

  private val absentee = "ABSENT (\\d+)".r("number")
  private val postal = "POSTAL (\\d+)".r("number")
  private val prepoll = "PRE_POLL (\\d+)".r("number")
  private val provisional = "PROVISIONAL (\\d+)".r("number")

  def fromFormalPreferencesRow(
                                election: SenateElectionForState,
                                rawPreferenceParser: RawPreferenceParser,
                                divisionNameLookup: String => Division,
                                pollingPlaceNameLookup: (State, String) => FederalPollingPlace,
                                row: FetchRawFormalSenatePreferences.Row,
                              ): SenateBallot = {
    val division = divisionNameLookup(row.electorateName)
    val state = election.state
    val federalElection = election.election.federalElection

    def voteCollectionPointFrom(voteCollectionPointName: String): FederalVcp = {
      voteCollectionPointName match {
        case absentee(number) => FederalVoteCollectionPoint.Special(
          federalElection,
          election.state,
          division,
          SpecialVcpType.Absentee,
          FederalVoteCollectionPoint.Special.Id(number.toInt),
        )
        case postal(number) => FederalVoteCollectionPoint.Special(
          federalElection,
          election.state,
          division,
          SpecialVcpType.Postal,
          FederalVoteCollectionPoint.Special.Id(number.toInt),
        )
        case prepoll(number) => FederalVoteCollectionPoint.Special(
          federalElection,
          election.state,
          division,
          SpecialVcpType.PrePoll,
          FederalVoteCollectionPoint.Special.Id(number.toInt),
        )
        case provisional(number) => FederalVoteCollectionPoint.Special(
          federalElection,
          election.state,
          division,
          SpecialVcpType.Provisional,
          FederalVoteCollectionPoint.Special.Id(number.toInt),
        )
        case _ => pollingPlaceNameLookup(state, voteCollectionPointName)
      }
    }

    val (atlPrefs, btlPrefs) = rawPreferenceParser.preferencesFrom(row.preferences)

    SenateBallot(
      election,
      FederalBallotJurisdiction(
        election.state,
        division,
        voteCollectionPointFrom(row.voteCollectionPointName),
      ),
      SenateBallotId(
        row.batchNumber,
        row.paperNumber,
      ),
      atlPrefs,
      btlPrefs
    )
  }

}
