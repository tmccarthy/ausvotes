package au.id.tmm.senatedb.core.parsing

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.{Ballot, Division, PollingPlace, VoteCollectionPoint}
import au.id.tmm.senatedb.core.rawdata.model.FormalPreferencesRow
import au.id.tmm.utilities.geo.australia.State

object BallotGeneration {

  private val absentee = "ABSENT (\\d+)".r("number")
  private val postal = "POSTAL (\\d+)".r("number")
  private val prepoll = "PRE_POLL (\\d+)".r("number")
  private val provisional = "PROVISIONAL (\\d+)".r("number")

  def fromFormalPreferencesRow(election: SenateElection,
                               state: State,
                               rawPreferenceParser: RawPreferenceParser,
                               divisionNameLookup: String => Division,
                               pollingPlaceNameLookup: (State, String) => PollingPlace,
                               row: FormalPreferencesRow): Ballot = {
    val division = divisionNameLookup(row.electorateName)

    def voteCollectionPointFrom(voteCollectionPointName: String) = {
      voteCollectionPointName match {
        case absentee(number) => VoteCollectionPoint.Absentee(election, state, division, number.toInt)
        case postal(number) => VoteCollectionPoint.Postal(election, state, division, number.toInt)
        case prepoll(number) => VoteCollectionPoint.PrePoll(election, state, division, number.toInt)
        case provisional(number) => VoteCollectionPoint.Provisional(election, state, division, number.toInt)
        case _ => pollingPlaceNameLookup(state, voteCollectionPointName)
      }
    }

    val (atlPrefs, btlPrefs) = rawPreferenceParser.preferencesFrom(row.preferences)

    Ballot(
      election,
      state,
      division,
      voteCollectionPointFrom(row.voteCollectionPointName),
      row.batchNumber,
      row.paperNumber,
      atlPrefs,
      btlPrefs
    )
  }

}
