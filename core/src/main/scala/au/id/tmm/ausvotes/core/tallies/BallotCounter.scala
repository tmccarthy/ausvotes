package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.computations.BallotWithFacts
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion.{Exhausted, NotExhausted}
import au.id.tmm.ausvotes.core.model.parsing.Ballot.AtlPreferences
import au.id.tmm.ausvotes.core.model.parsing.Preference

trait BallotCounter {

  def weigh(ballots: Iterable[BallotWithFacts]): Double

  def name: String

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

    override val name: String = "formal ballots"
  }

  case object VotedAtl extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.normalisedBallot.isNormalisedToAtl

    override val name: String = "votes atl"
  }

  case object VotedAtlAndBtl extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean =
      ballot.normalisedBallot.isFormalAtl && ballot.normalisedBallot.isFormalBtl

    override val name: String = "votes atl and btl"
  }

  case object VotedBtl extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.normalisedBallot.isNormalisedToBtl

    override val name: String = "votes btl"
  }

  case object DonkeyVotes extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.isDonkeyVote

    override val name: String = "donkey votes"
  }

  case object ExhaustedBallots extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.exhaustion match {
      case _: Exhausted => true
      case NotExhausted => false
    }

    override val name: String = "exhausted ballots"
  }

  case object ExhaustedVotes extends BallotCounter {
    override def weigh(ballots: Iterable[BallotWithFacts]): Double = {
      ballots
        .map {
          _.exhaustion match {
            case BallotExhaustion.Exhausted(_, value, _) => value.factor
            case BallotExhaustion.NotExhausted => 0d
          }
        }
        .sum
    }

    override val name: String = "exhausted votes"
  }

  case object UsedHowToVoteCard extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.matchingHowToVote.isDefined


    override val name: String = "votes using htv cards"
  }

  case object Voted1Atl extends PredicateBallotCounter {
    private val oneAtlPreferences: Set[Preference] = Set(Preference.Numbered(1), Preference.Tick, Preference.Cross)

    override def isCounted(ballotWithFacts: BallotWithFacts): Boolean = {
      val ballot = ballotWithFacts.ballot

      ballot.btlPreferences.isEmpty && hasOnly1Atl(ballot.atlPreferences)
    }

    private def hasOnly1Atl(atlPreferences: AtlPreferences) =
      atlPreferences.size == 1 && oneAtlPreferences.contains(atlPreferences.head._2)


    override val name: String = "votes 1 atl"
  }

  case object UsedSavingsProvision extends PredicateBallotCounter {
    override def isCounted(ballot: BallotWithFacts): Boolean = ballot.savingsProvisionsUsed.nonEmpty


    override val name: String = "ballots using savings provisions"
  }
}