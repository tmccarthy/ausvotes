package au.id.tmm.ausvotes.core.computations.firstpreference

import au.id.tmm.ausvotes.model.instances.BallotNormalisationResultInstances.Ops
import au.id.tmm.ausvotes.model.stv.{FirstPreference, Group, NormalisedBallot, StvCandidate}

object FirstPreferenceCalculator {

  def firstPreferenceOf[E](normalisedBallot: NormalisedBallot[E]): FirstPreference[E] = {
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

  private def firstPreferenceAtl[E](atlOrder: Vector[Group[E]]): Option[FirstPreference[E]] = atlOrder.headOption.map {
    g => FirstPreference(g, g.party)
  }

  private def firstPreferenceBtl[E](btlOrder: Vector[StvCandidate[E]]): Option[FirstPreference[E]] = btlOrder.headOption.map {
    c => FirstPreference(c.position.group, c.candidateDetails.party)
  }

}
