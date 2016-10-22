package au.id.tmm.senatedb.computations.firstpreference

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.computation.NormalisedBallot
import au.id.tmm.senatedb.model.parsing._
import au.id.tmm.utilities.geo.australia.State

class FirstPreferenceCalculator(election: SenateElection, state: State, candidates: Set[Candidate]) {

  private val candidatePerPosition: Map[CandidatePosition, Candidate] = candidates
    .filter(_.election == election)
    .filter(_.state == state)
    .groupBy(_.btlPosition)
    .mapValues(_.head)

  def firstPreferencedPartyOf(normalisedBallot: NormalisedBallot): Option[Party] = {
    require(normalisedBallot.isFormal)

    if (normalisedBallot.isNormalisedToAtl) {
      firstPreferencedPartyAtl(normalisedBallot)
    } else {
      firstPreferencedPartyBtl(normalisedBallot)
    }
  }

  private def firstPreferencedPartyAtl(normalisedBallot: NormalisedBallot): Option[Party] = {
    normalisedBallot.canonicalOrder.head.group match {
      case Group(_, _, _, party) => party
      case Ungrouped => None
    }
  }

  private def firstPreferencedPartyBtl(normalisedBallot: NormalisedBallot): Option[Party] = {
    val firstPreferencedPositionBtl = normalisedBallot.canonicalOrder.head

    val firstPreferencedCandidate = candidatePerPosition(firstPreferencedPositionBtl)

    firstPreferencedCandidate.party
  }
}

object FirstPreferenceCalculator {
  def apply(election: SenateElection, state: State, candidates: Set[Candidate]): FirstPreferenceCalculator =
    new FirstPreferenceCalculator(election, state, candidates)
}