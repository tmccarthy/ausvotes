package au.id.tmm.senatedb.parsing

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.{Ballot, Division, PollingPlace}
import au.id.tmm.senatedb.rawdata.model.FormalPreferencesRow
import au.id.tmm.utilities.geo.australia.State

object BallotGeneration {

  def fromFormalPreferencesRow(election: SenateElection,
                               state: State,
                               rawPreferenceParser: RawPreferenceParser,
                               divisionNameLookup: String => Division,
                               pollingPlaceNameLookup: String => PollingPlace,
                               row: FormalPreferencesRow): Ballot = {
    val (atlPrefs, btlPrefs) = rawPreferenceParser.preferencesFrom(row.preferences)

    Ballot(
      election,
      state,
      divisionNameLookup(row.electorateName),
      pollingPlaceNameLookup(row.voteCollectionPointName),
      row.batchNumber,
      row.paperNumber,
      atlPrefs,
      btlPrefs
    )
  }

}
