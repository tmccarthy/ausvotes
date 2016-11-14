package au.id.tmm.senatedb.computations.firstpreference

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.computation.{FirstPreference, NormalisedBallot}
import au.id.tmm.senatedb.model.parsing._
import au.id.tmm.utilities.geo.australia.State

class FirstPreferenceCalculator(election: SenateElection, state: State, candidates: Set[Candidate]) {

  private val candidatePerPosition: Map[CandidatePosition, Candidate] = candidates
    .filter(_.election == election)
    .filter(_.state == state)
    .groupBy(_.btlPosition)
    .mapValues(_.head)

  def firstPreferenceOf(normalisedBallot: NormalisedBallot): FirstPreference = {
    require(normalisedBallot.isFormal)

    if (normalisedBallot.isNormalisedToAtl) {
      firstPreferenceAtl(normalisedBallot)
    } else {
      firstPreferenceBtl(normalisedBallot)
    }
  }

  private def firstPreferenceAtl(normalisedBallot: NormalisedBallot): FirstPreference = {
    normalisedBallot.canonicalOrder.head.group match {
      case g: Group => FirstPreference(g, g.party)
    }
  }

  private def firstPreferenceBtl(normalisedBallot: NormalisedBallot): FirstPreference = {
    val firstPreferencedPositionBtl = normalisedBallot.canonicalOrder.head

    val firstPreferencedCandidate = candidatePerPosition(firstPreferencedPositionBtl)

    FirstPreference(firstPreferencedPositionBtl.group, firstPreferencedCandidate.party)
  }
}

object FirstPreferenceCalculator {
  def apply(election: SenateElection, state: State, candidates: Set[Candidate]): FirstPreferenceCalculator =
    new FirstPreferenceCalculator(election, state, candidates)
}