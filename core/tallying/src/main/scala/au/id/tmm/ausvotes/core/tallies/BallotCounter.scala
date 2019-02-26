package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.computations.StvBallotWithFacts
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion.{Exhausted, NotExhausted}
import au.id.tmm.ausvotes.model.instances.BallotNormalisationResultInstances.Ops
import au.id.tmm.countstv.normalisation.Preference

sealed trait BallotCounter {

  def weigh(ballots: Iterable[StvBallotWithFacts[_, _, _]]): Double

  def name: String

}

object BallotCounter {
  sealed trait PredicateBallotCounter extends BallotCounter {
    override def weigh(ballots: Iterable[StvBallotWithFacts[_, _, _]]): Double = {
      ballots
        .count(isCounted)
        .toDouble
    }

    def isCounted(ballot: StvBallotWithFacts[_, _, _]): Boolean
  }

  case object FormalBallots extends PredicateBallotCounter {
    override def isCounted(ballot: StvBallotWithFacts[_, _, _]): Boolean = ballot.normalisedBallot.canonicalOrder.isDefined

    override val name: String = "formal ballots"
  }

  case object VotedAtl extends PredicateBallotCounter {
    override def isCounted(ballot: StvBallotWithFacts[_, _, _]): Boolean = ballot.normalisedBallot.isNormalisedToAtl

    override val name: String = "votes atl"
  }

  case object VotedAtlAndBtl extends PredicateBallotCounter {
    override def isCounted(ballot: StvBallotWithFacts[_, _, _]): Boolean =
      ballot.normalisedBallot.atl.isSavedOrFormal && ballot.normalisedBallot.btl.isSavedOrFormal

    override val name: String = "votes atl and btl"
  }

  case object VotedBtl extends PredicateBallotCounter {
    override def isCounted(ballot: StvBallotWithFacts[_, _, _]): Boolean = ballot.normalisedBallot.isNormalisedToBtl

    override val name: String = "votes btl"
  }

  case object DonkeyVotes extends PredicateBallotCounter {
    override def isCounted(ballot: StvBallotWithFacts[_, _, _]): Boolean = ballot.isDonkeyVote

    override val name: String = "donkey votes"
  }

  case object ExhaustedBallots extends PredicateBallotCounter {
    override def isCounted(ballot: StvBallotWithFacts[_, _, _]): Boolean = ballot.exhaustion match {
      case _: Exhausted => true
      case NotExhausted => false
    }

    override val name: String = "exhausted ballots"
  }

  case object ExhaustedVotes extends BallotCounter {
    override def weigh(ballots: Iterable[StvBallotWithFacts[_, _, _]]): Double = {
      ballots
        .map {
          _.exhaustion match {
            case BallotExhaustion.Exhausted(_, value, _) => value.factor.toDouble
            case BallotExhaustion.NotExhausted => 0d
          }
        }
        .sum
    }

    override val name: String = "exhausted votes"
  }

  case object UsedHowToVoteCard extends PredicateBallotCounter {
    override def isCounted(ballot: StvBallotWithFacts[_, _, _]): Boolean = ballot.matchingHowToVote.isDefined


    override val name: String = "votes using htv cards"
  }

  case object Voted1Atl extends PredicateBallotCounter {
    private val oneAtlPreferences: Set[Preference] = Set(Preference.Numbered(1), Preference.Tick, Preference.Cross)

    override def isCounted(ballotWithFacts: StvBallotWithFacts[_, _, _]): Boolean = {
      val ballot = ballotWithFacts.ballot

      ballot.candidatePreferences.isEmpty && hasOnly1Atl(ballot.groupPreferences)
    }

    private def hasOnly1Atl(atlPreferences: Map[_, Preference]) =
      atlPreferences.size == 1 && oneAtlPreferences.contains(atlPreferences.head._2)


    override val name: String = "votes 1 atl"
  }

  case object UsedSavingsProvision extends PredicateBallotCounter {
    override def isCounted(ballot: StvBallotWithFacts[_, _, _]): Boolean = ballot.savingsProvisionsUsed.nonEmpty


    override val name: String = "ballots using savings provisions"
  }
}