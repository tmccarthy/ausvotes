package au.id.tmm.senatedb.core.computations.savings

import au.id.tmm.senatedb.core.model.computation.{NormalisedBallot, SavingsProvision}
import au.id.tmm.senatedb.core.model.parsing.{Ballot, Preference}

object SavingsComputation {

  def savingsProvisionsUsedBy(ballot: Ballot, normalised: NormalisedBallot): Set[SavingsProvision] = {
    Stream(
      markUsageProvisionsUsedBy(ballot, normalised),
      insufficientSquaresProvisionsUsedBy(ballot, normalised),
      countErrorSavingsProvisionsUsedBy(ballot, normalised)
    )
      .flatten
      .toSet
  }

  private def markUsageProvisionsUsedBy(ballot: Ballot, normalised: NormalisedBallot): Option[SavingsProvision.UsedMark] = {

    if (normalised.isNormalisedToAtl) {
      markUsageOf(ballot.atlPreferences.values)
    } else {
      markUsageOf(ballot.btlPreferences.values)
    }
  }

  private def markUsageOf(preferences: Iterable[Preference]): Option[SavingsProvision.UsedMark] = {
    for (preference <- preferences) {
      if (preference == Preference.Tick) {
        return Some(SavingsProvision.UsedTick)
      } else if (preference == Preference.Cross) {
        return Some(SavingsProvision.UsedCross)
      }
    }

    None
  }

  private def countErrorSavingsProvisionsUsedBy(ballot: Ballot,
                                                normalised: NormalisedBallot
                                               ): Option[SavingsProvision.CountingError] = {
    def hasCountErrorAtl = ballot.atlPreferences.size > normalised.atlFormalPreferenceCount
    def hasCountErrorBtl = ballot.btlPreferences.size > normalised.btlFormalPreferenceCount

    if (normalised.isNormalisedToAtl && hasCountErrorAtl) {
      Some(SavingsProvision.CountingErrorAtl)

    } else if (normalised.isNormalisedToBtl && hasCountErrorBtl) {
      Some(SavingsProvision.CountingErrorBtl)

    } else {
      None
    }
  }

  private def insufficientSquaresProvisionsUsedBy(ballot: Ballot,
                                                  normalised: NormalisedBallot
                                                 ): Option[SavingsProvision.InsufficientPreferences] = {
    def markedInsufficientSquaresAtl = normalised.atlFormalPreferenceCount < 6
    def markedInsufficientSquaresBtl = normalised.btlFormalPreferenceCount < 12

    if (normalised.isNormalisedToAtl && markedInsufficientSquaresAtl) {
      Some(SavingsProvision.InsufficientPreferencesAtl)

    } else if (normalised.isNormalisedToBtl && markedInsufficientSquaresBtl) {
      Some(SavingsProvision.InsufficientPreferencesBtl)

    } else {
      None
    }
  }
}
