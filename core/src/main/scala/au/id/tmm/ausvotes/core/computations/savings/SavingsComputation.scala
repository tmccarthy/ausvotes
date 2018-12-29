package au.id.tmm.ausvotes.core.computations.savings

import au.id.tmm.ausvotes.core.model.computation.{NormalisedBallot, SavingsProvision}
import au.id.tmm.ausvotes.model.Preference
import au.id.tmm.ausvotes.model.federal.senate.SenateBallot

object SavingsComputation {

  def savingsProvisionsUsedBy(ballot: SenateBallot, normalised: NormalisedBallot): Set[SavingsProvision] = {
    Stream(
      markUsageProvisionsUsedBy(ballot, normalised),
      insufficientSquaresProvisionsUsedBy(ballot, normalised),
      countErrorSavingsProvisionsUsedBy(ballot, normalised)
    )
      .flatten
      .toSet
  }

  private def markUsageProvisionsUsedBy(ballot: SenateBallot, normalised: NormalisedBallot): Option[SavingsProvision.UsedMark] = {

    if (normalised.isNormalisedToAtl) {
      markUsageOf(ballot.groupPreferences.values)
    } else {
      markUsageOf(ballot.candidatePreferences.values)
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

  private def countErrorSavingsProvisionsUsedBy(ballot: SenateBallot,
                                                normalised: NormalisedBallot
                                               ): Option[SavingsProvision.CountingError] = {
    def hasCountErrorAtl = ballot.groupPreferences.size > normalised.atlFormalPreferenceCount
    def hasCountErrorBtl = ballot.candidatePreferences.size > normalised.btlFormalPreferenceCount

    if (normalised.isNormalisedToAtl && hasCountErrorAtl) {
      Some(SavingsProvision.CountingErrorAtl)

    } else if (normalised.isNormalisedToBtl && hasCountErrorBtl) {
      Some(SavingsProvision.CountingErrorBtl)

    } else {
      None
    }
  }

  private def insufficientSquaresProvisionsUsedBy(ballot: SenateBallot,
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
