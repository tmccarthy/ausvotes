package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.formalpreferences

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.computations.expiry.ExhaustionCalculator
import au.id.tmm.senatedb.data.database.model.{AtlPreferencesRow, BallotFactsRow, BallotRow, BtlPreferencesRow}

class BallotFactsCalculator(ballotNormaliser: BallotNormaliser,
                            exhaustionCalculator: ExhaustionCalculator) {

  def ballotFactsOf(ballotRow: BallotRow,
                    atlPreferences: Set[AtlPreferencesRow],
                    btlPreferences: Set[BtlPreferencesRow]): BallotFactsRow = {
    val numAtlPreferences = atlPreferences.flatMap(_.preference).reduceOption(_ max _).getOrElse(0)
    val numBtlPreferences = btlPreferences.flatMap(_.preference).reduceOption(_ max _).getOrElse(0)

    val usedSymbolAtl = atlPreferences.exists(_.mark.isDefined)
    val usedSymbolBtl = btlPreferences.exists(_.mark.isDefined)

    val normalised = ballotNormaliser.normalise(atlPreferences, btlPreferences)

    assert(normalised.isDefined, "Encountered an informal ballot in the AEC data")

    val exhaustion = exhaustionCalculator.computeExhaustionOf(normalised.get)

    BallotFactsRow(
      ballotId = ballotRow.ballotId,
      numAtlPreferences = numAtlPreferences,
      numBtlPreferences = numBtlPreferences,
      atlUsedSymbols = usedSymbolAtl,
      btlUsedSymbols = usedSymbolBtl,
      exhaustedAtCount = exhaustion.map(_.atCount),
      candidatesElectedAtExhaustion = exhaustion.map(_.candidatesElected)
    )
  }

}
