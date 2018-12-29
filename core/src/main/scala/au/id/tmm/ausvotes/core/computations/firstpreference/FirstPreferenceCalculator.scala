package au.id.tmm.ausvotes.core.computations.firstpreference

import au.id.tmm.ausvotes.core.model.computation.{FirstPreference, NormalisedBallot}
import au.id.tmm.ausvotes.model.federal.senate.{SenateElectionForState, SenateGroup}
import au.id.tmm.ausvotes.model.stv.Ungrouped

object FirstPreferenceCalculator {

  def firstPreferenceOf(normalisedBallot: NormalisedBallot): FirstPreference = {
    require(normalisedBallot.isFormal)

    if (normalisedBallot.isNormalisedToAtl) {
      firstPreferenceAtl(normalisedBallot)
    } else {
      firstPreferenceBtl(normalisedBallot)
    }
  }

  private def firstPreferenceAtl(normalisedBallot: NormalisedBallot): FirstPreference = {
    normalisedBallot.canonicalOrder.head.position.group match {
      case g: SenateGroup => FirstPreference(g, g.party)
      case ungrouped: Ungrouped[SenateElectionForState] => //noinspection NotImplementedCode
        ??? // Impossible TODO redesign types to make this unnecessary
    }
  }

  private def firstPreferenceBtl(normalisedBallot: NormalisedBallot): FirstPreference = {
    val firstPreferencedCandidate = normalisedBallot.canonicalOrder.head

    FirstPreference(firstPreferencedCandidate.position.group, firstPreferencedCandidate.candidate.party)
  }
}
