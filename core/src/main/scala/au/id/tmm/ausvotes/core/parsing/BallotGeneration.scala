package au.id.tmm.ausvotes.core.parsing

import au.id.tmm.ausvotes.core.rawdata.model.FormalPreferencesRow
import au.id.tmm.ausvotes.model.VoteCollectionPoint
import au.id.tmm.ausvotes.model.VoteCollectionPoint.Special.SpecialVcpType
import au.id.tmm.ausvotes.model.federal._
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallot, SenateBallotId, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State

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
                                row: FormalPreferencesRow,
                              ): SenateBallot = {
    val division = divisionNameLookup(row.electorateName)
    val state = election.state
    val federalElection = election.election.federalElection

    def voteCollectionPointFrom(voteCollectionPointName: String): FederalVcp = {
      voteCollectionPointName match {
        case absentee(number) => VoteCollectionPoint.Special(
          federalElection,
          FederalVcpJurisdiction(election.state, division),
          SpecialVcpType.Absentee,
          VoteCollectionPoint.Special.Id(number.toInt),
        )
        case postal(number) => VoteCollectionPoint.Special(
          federalElection,
          FederalVcpJurisdiction(election.state, division),
          SpecialVcpType.Postal,
          VoteCollectionPoint.Special.Id(number.toInt),
        )
        case prepoll(number) => VoteCollectionPoint.Special(
          federalElection,
          FederalVcpJurisdiction(election.state, division),
          SpecialVcpType.PrePoll,
          VoteCollectionPoint.Special.Id(number.toInt),
        )
        case provisional(number) => VoteCollectionPoint.Special(
          federalElection,
          FederalVcpJurisdiction(election.state, division),
          SpecialVcpType.Provisional,
          VoteCollectionPoint.Special.Id(number.toInt),
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
