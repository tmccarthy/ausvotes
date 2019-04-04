package au.id.tmm.ausvotes.core.tallies.redo

import au.id.tmm.ausvotes.core.computations.SenateBallotWithFacts
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion.{Exhausted, NotExhausted}
import au.id.tmm.ausvotes.core.tallies.Tally
import au.id.tmm.ausvotes.model.instances.BallotNormalisationResultInstances.Ops
import au.id.tmm.countstv.normalisation.Preference
import cats.Monoid
import cats.instances.double.catsKernelStdGroupForDouble
import cats.instances.long.catsKernelStdGroupForLong
import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}

import scala.collection.mutable

sealed abstract class BallotTallier[-B, A : Monoid] {
  def tally(ballot: B): A

  def tallyAll(ballots: Iterable[B]): A = Monoid.combineAll(ballots.toIterator.map(tally))

  def groupingBy[G, B1 <: B](ballotGrouper: BallotGrouper[B1, G]): BallotTallier.GroupingTallier[B1, G, A] =
    BallotTallier.GroupingTallier.apply[B1, G, A](ballotGrouper, this)
}

object BallotTallier {

  private implicit val monoidForUnit: Monoid[Unit] = new Monoid[Unit] {
    override def empty: Unit = ()

    override def combine(left: Unit, right: Unit): Unit = ()
  }

  case object UnitBallotTallier extends BallotTallier[Any, Unit] {
    override def tallyAll(ballots: Iterable[Any]): Unit = ()

    override def tally(ballot: Any): Unit = ()
  }

  final case class GroupingTallier[-B, G, A : Monoid](
                                                       grouper: BallotGrouper[B, G],
                                                       underlyingTallier: BallotTallier[B, A],
                                                     ) extends BallotTallier[B, Tally[G, A]] {
    override def tallyAll(ballots: Iterable[B]): Tally[G, A] = {
      val mapBuilder: mutable.Map[G, A] = mutable.Map[G, A]()

      mapBuilder.sizeHint(ballots.size)

      for (ballot <- ballots) {
        val groups = grouper.groupsOf(ballot)
        val count = underlyingTallier.tally(ballot)

        for (group <- groups) {
          val existingValue = mapBuilder.getOrElse(group, Monoid[A].empty)

          mapBuilder.update(group, Monoid[A].combine(existingValue, count))
        }
      }

      Tally(mapBuilder.toMap)
    }

    override def tally(ballot: B): Tally[G, A] = tallyAll(List(ballot))
  }

  sealed abstract class SenateBallotTallier[A : Monoid] extends BallotTallier[SenateBallotWithFacts, A]

  object SenateBallotTallier {
    case object FormalBallots extends SenateBallotTallier[Long] {
      override def tally(ballot: SenateBallotWithFacts): Long =
        if (ballot.normalisedBallot.canonicalOrder.isDefined) 1 else 0
    }

    case object VotedAtl extends SenateBallotTallier[Long] {
      override def tally(ballot: SenateBallotWithFacts): Long =
        if (ballot.normalisedBallot.isNormalisedToAtl) 1 else 0
    }

    case object VotedAtlAndBtl extends SenateBallotTallier[Long] {
      override def tally(ballot: SenateBallotWithFacts): Long =
        if (ballot.normalisedBallot.atl.isSavedOrFormal && ballot.normalisedBallot.btl.isSavedOrFormal) 1 else 0
    }

    case object VotedBtl extends SenateBallotTallier[Long] {
      override def tally(ballot: SenateBallotWithFacts): Long =
        if (ballot.normalisedBallot.isNormalisedToBtl) 1 else 0
    }

    case object DonkeyVotes extends SenateBallotTallier[Long] {
      override def tally(ballot: SenateBallotWithFacts): Long =
        if (ballot.isDonkeyVote) 1 else 0
    }

    case object ExhaustedBallots extends SenateBallotTallier[Long] {
      override def tally(ballot: SenateBallotWithFacts): Long =
        ballot.exhaustion match {
          case _: Exhausted => 1
          case NotExhausted => 0
        }
    }

    case object ExhaustedVotes extends SenateBallotTallier[Double] {
      override def tally(ballot: SenateBallotWithFacts): Double =
        ballot.exhaustion match {
          case BallotExhaustion.Exhausted(_, value, _) => value.factor.toDouble
          case BallotExhaustion.NotExhausted => 0d
        }
    }

    case object UsedHowToVoteCard extends SenateBallotTallier[Long] {
      override def tally(ballot: SenateBallotWithFacts): Long =
        if (ballot.matchingHowToVote.isDefined) 1 else 0
    }

    case object Voted1Atl extends SenateBallotTallier[Long] {
      override def tally(ballot: SenateBallotWithFacts): Long =
        if (ballot.ballot.candidatePreferences.isEmpty && ballot.ballot.groupPreferences.size == 1 && (ballot.ballot.groupPreferences.head._2 == Preference.Numbered(1) || ballot.ballot.groupPreferences.head._2 == Preference.Tick || ballot.ballot.groupPreferences.head._2 == Preference.Cross)) 1 else 0
    }

    case object UsedSavingsProvision extends SenateBallotTallier[Long] {
      override def tally(ballot: SenateBallotWithFacts): Long =
        if (ballot.savingsProvisionsUsed.nonEmpty) 1 else 0
    }

    implicit def encoder[A]: Encoder[SenateBallotTallier[A]] = {
      case FormalBallots => "formal_ballots".asJson
      case VotedAtl => "voted_atl".asJson
      case VotedAtlAndBtl => "voted_atl_and_btl".asJson
      case VotedBtl => "voted_btl".asJson
      case DonkeyVotes => "donkey_votes".asJson
      case ExhaustedBallots => "exhausted_ballots".asJson
      case ExhaustedVotes => "exhausted_votes".asJson
      case UsedHowToVoteCard => "used_how_to_vote_card".asJson
      case Voted1Atl => "voted_1_atl".asJson
      case UsedSavingsProvision => "used_savings_provision".asJson
    }

  }

  implicit def encoder[B, A]: Encoder[BallotTallier[B, A]] = {
    case UnitBallotTallier => "unit".asJson
    case GroupingTallier(grouper, underlyingTallier) => Json.obj(
      "grouper" -> BallotGrouper.encoder.apply(grouper),
      "tallier" -> BallotTallier.encoder.apply(underlyingTallier),
    )
    case t: SenateBallotTallier[A] => SenateBallotTallier.encoder.apply(t)
  }

}
