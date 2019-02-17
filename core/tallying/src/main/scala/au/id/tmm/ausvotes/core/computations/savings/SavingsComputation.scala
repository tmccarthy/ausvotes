package au.id.tmm.ausvotes.core.computations.savings

import au.id.tmm.ausvotes.core.model.computation.SavingsProvision
import au.id.tmm.ausvotes.model.federal.senate.{NormalisedSenateBallot, SenateBallot}
import au.id.tmm.countstv.normalisation.BallotNormalisation.Result
import au.id.tmm.countstv.normalisation.BallotNormalisationRule

object SavingsComputation {

  def savingsProvisionsUsedBy(ballot: SenateBallot, normalised: NormalisedSenateBallot): Set[SavingsProvision] = {
    val rulesViolated =
      if (normalised.isNormalisedToBtl) {
        normalised.btl match {
          case Result.Formal(_) | Result.Informal(_, _, _) => Set.empty
          case Result.Saved(_, rulesViolated) => rulesViolated
        }
      } else if (normalised.isNormalisedToAtl) {
        normalised.atl match {
          case Result.Formal(_) | Result.Informal(_, _, _) => Set.empty
          case Result.Saved(_, rulesViolated) => rulesViolated
        }
      } else {
        Set.empty
      }

    rulesViolated.map {
      case BallotNormalisationRule.MinimumPreferences(_) => SavingsProvision.InsufficientPreferences
      case BallotNormalisationRule.TicksForbidden => SavingsProvision.UsedTick
      case BallotNormalisationRule.CrossesForbidden => SavingsProvision.UsedCross
      case BallotNormalisationRule.CountingErrorsForbidden => SavingsProvision.CountingError
    }
  }

}
