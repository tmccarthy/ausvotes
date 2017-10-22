package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.computations.BallotWithFacts
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion.{Exhausted, ExhaustedBeforeInitialAllocation, NotExhausted}
import au.id.tmm.ausvotes.core.model.parsing.Ballot.AtlPreferences
import au.id.tmm.ausvotes.core.model.parsing.Preference

trait BallotCounter {

  def weigh(ballots: Iterable[BallotWithFacts]): Double

}

object BallotCounter {
  trait PredicateBallotCounter extends BallotCounter {
    override def weigh(ballots: Iterable[BallotWithFacts]): Double = {
      ballots
        .count(isCounted)
        .toDouble
    }

    def isCounted(ballot: BallotWithFacts): Boolean
  }

  case object FormalBallots extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.normalisedBallot.isFormal
  }

  case object VotedAtl extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.normalisedBallot.isNormalisedToAtl
  }

  case object VotedAtlAndBtl extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean =
      ballot.normalisedBallot.isFormalAtl && ballot.normalisedBallot.isFormalBtl
  }

  case object VotedBtl extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.normalisedBallot.isNormalisedToBtl
  }

  case object DonkeyVotes extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.isDonkeyVote
  }

  case object ExhaustedBallots extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.exhaustion match {
      case _: Exhausted | ExhaustedBeforeInitialAllocation => true
      case NotExhausted => false
    }
  }

  case object ExhaustedVotes extends BallotCounter {
    override def weigh(ballots: Iterable[BallotWithFacts]): Double = {
      ballots
        .map {
          _.exhaustion match {
            case BallotExhaustion.Exhausted(_, value, _) => value
            case BallotExhaustion.NotExhausted => 0d
            case ExhaustedBeforeInitialAllocation => 1d
          }
        }
        .sum
    }
  }

  case object UsedHowToVoteCard extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.matchingHowToVote.isDefined
  }

  case object Voted1Atl extends PredicateBallotCounter {
    private val oneAtlPreferences: Set[Preference] = Set(Preference.Numbered(1), Preference.Tick, Preference.Cross)

    override def isCounted(ballotWithFacts: BallotWithFacts): Boolean = {
      val ballot = ballotWithFacts.ballot

      ballot.btlPreferences.isEmpty && hasOnly1Atl(ballot.atlPreferences)
    }

    private def hasOnly1Atl(atlPreferences: AtlPreferences) =
      atlPreferences.size == 1 && oneAtlPreferences.contains(atlPreferences.head._2)
  }

  case object UsedSavingsProvision extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.savingsProvisionsUsed.nonEmpty
  }
}