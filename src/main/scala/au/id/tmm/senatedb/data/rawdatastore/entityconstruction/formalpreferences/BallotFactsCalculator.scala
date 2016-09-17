package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.formalpreferences

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.computations.expiry.ExhaustionCalculator
import au.id.tmm.senatedb.data.database.model.{AtlPreferencesRow, BallotFactsRow, BallotRow, BtlPreferencesRow}
import au.id.tmm.senatedb.model.{GroupUtils, Preferenceable}

class BallotFactsCalculator(ballotNormaliser: BallotNormaliser,
                            exhaustionCalculator: ExhaustionCalculator) {

  def ballotFactsOf(ballotRow: BallotRow,
                    atlPreferences: Set[AtlPreferencesRow],
                    btlPreferences: Set[BtlPreferencesRow]): BallotFactsRow = {
    def count(preferences: Set[_ <: Preferenceable]): Int = preferences.count(_.hasPreference)

    val numAtlPreferences = count(atlPreferences)
    val numBtlPreferences = count(btlPreferences)

    val usedSymbolAtl = atlPreferences.exists(_.mark.isDefined)
    val usedSymbolBtl = btlPreferences.exists(_.mark.isDefined)

    val normalised = ballotNormaliser.normalise(atlPreferences, btlPreferences)

    assert(normalised.isDefined, "Encountered an informal ballot in the AEC data")

    val exhaustion = exhaustionCalculator.computeExhaustionOf(normalised.get)

    val donkeyVote = isDonkeyVote(atlPreferences)

    BallotFactsRow(
      ballotId = ballotRow.ballotId,
      numAtlPreferences = numAtlPreferences,
      numBtlPreferences = numBtlPreferences,
      atlUsedSymbols = usedSymbolAtl,
      btlUsedSymbols = usedSymbolBtl,
      exhaustedAtCount = exhaustion.map(_.ordinal),
      candidatesElectedAtExhaustion = exhaustion.map(_.candidatesElected),
      donkeyVote
    )
  }

  private def isDonkeyVote(atlPreferences: Set[AtlPreferencesRow]): Boolean = {
    if (atlPreferences.size < BallotFactsCalculator.DONKEY_VOTE_THRESHOLD) {
      return false
    }

    val sortedByGroup = atlPreferences.toStream
      .sortBy(_.group)(GroupUtils.groupOrdering)

    for (row <- sortedByGroup) {
      val isPreferencedInOrder = row.preference.contains(GroupUtils.indexOfGroup(row.group) + 1)

      if (!isPreferencedInOrder) {
        return false
      }
    }

    true
  }
}

object BallotFactsCalculator {
  val DONKEY_VOTE_THRESHOLD = 3
}