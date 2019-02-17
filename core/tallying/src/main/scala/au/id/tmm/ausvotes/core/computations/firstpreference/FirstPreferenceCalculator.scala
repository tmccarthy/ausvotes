package au.id.tmm.ausvotes.core.computations.firstpreference

import au.id.tmm.ausvotes.core.model.computation.FirstPreference
import au.id.tmm.ausvotes.model.federal.senate.{NormalisedSenateBallot, SenateCandidate, SenateGroup}
import au.id.tmm.ausvotes.model.instances.BallotNormalisationResultInstances.Ops

object FirstPreferenceCalculator {

  def firstPreferenceOf(normalisedBallot: NormalisedSenateBallot): FirstPreference = {
    val maybeFirstPreference = if (normalisedBallot.isNormalisedToBtl) {
      normalisedBallot.btl.normalisedBallotIfFormal.flatMap(firstPreferenceBtl)
    } else if (normalisedBallot.isNormalisedToAtl) {
      normalisedBallot.atl.normalisedBallotIfFormal.flatMap(firstPreferenceAtl)
    } else {
      None
    }

     maybeFirstPreference.getOrElse {
      // TODO do this better
      throw new IllegalStateException(s"Could not find the first preference of ballot $normalisedBallot")
    }
  }

  private def firstPreferenceAtl(atlOrder: Vector[SenateGroup]): Option[FirstPreference] = atlOrder.headOption.map {
    g: SenateGroup => FirstPreference(g, g.party)
  }

  private def firstPreferenceBtl(btlOrder: Vector[SenateCandidate]): Option[FirstPreference] = btlOrder.headOption.map {
    c => FirstPreference(c.position.group, c.candidateDetails.party)
  }

}
