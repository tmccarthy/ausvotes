package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.computations.StvBallotWithFacts
import au.id.tmm.ausvotes.core.computations.parties.PartyEquivalenceComputation
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion.{Exhausted, NotExhausted}
import au.id.tmm.ausvotes.core.model.computation.{BallotExhaustion, SavingsProvision}
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallotGroup, SenateBallotId, SenateElection, SenateElectionForState}
import au.id.tmm.ausvotes.model.federal.{Division, FederalBallotJurisdiction, FederalVcp}
import au.id.tmm.ausvotes.model.instances.BallotNormalisationResultInstances.Ops
import au.id.tmm.countstv.normalisation.Preference
import au.id.tmm.utilities.geo.australia.State
import cats.Monoid
import cats.instances.double._
import cats.instances.long._
import io.circe.Encoder
import io.circe.syntax.EncoderOps

object SenateElectionTalliers {

  sealed trait BallotGrouping[G] extends Grouper[G, StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]]

  object BallotGrouping {
    case object SenateElection extends BallotGrouping[SenateElection] {
      override def groupsOf(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Set[SenateElection] = Set(ballot.ballot.election.election)
    }

    case object State extends BallotGrouping[State] {
      override def groupsOf(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Set[State] = Set(ballot.ballot.jurisdiction.state)
    }

    case object Division extends BallotGrouping[Division] {
      override def groupsOf(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Set[Division] = Set(ballot.ballot.jurisdiction.electorate)
    }

    case object VoteCollectionPoint extends BallotGrouping[FederalVcp] {
      override def groupsOf(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Set[FederalVcp] = Set(ballot.ballot.jurisdiction.voteCollectionPoint)
    }

    case object FirstPreferencedPartyNationalEquivalent extends BallotGrouping[Option[Party]] {
      override def groupsOf(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Set[Option[Party]] = Set(ballot.firstPreference.party.map(PartyEquivalenceComputation.nationalEquivalentOf))
    }

    case object FirstPreferencedParty extends BallotGrouping[Option[Party]] {
      override def groupsOf(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Set[Option[Party]] = Set(ballot.firstPreference.party)
    }

    case object FirstPreferencedGroup extends BallotGrouping[SenateBallotGroup] {
      override def groupsOf(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Set[SenateBallotGroup] = Set(ballot.firstPreference.group)
    }

    case object UsedSavingsProvision extends BallotGrouping[SavingsProvision] {
      override def groupsOf(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Set[SavingsProvision] = ballot.savingsProvisionsUsed
    }

    implicit def encoder[G]: Encoder[BallotGrouping[G]] = {
      case SenateElection => "senate_election".asJson
      case State => "state".asJson
      case Division => "division".asJson
      case VoteCollectionPoint => "vote_collection_point".asJson
      case FirstPreferencedPartyNationalEquivalent => "first_preferenced_party_national_equivalent".asJson
      case FirstPreferencedParty => "first_preferenced_party".asJson
      case FirstPreferencedGroup => "first_preferenced_group".asJson
      case UsedSavingsProvision => "used_savings_provision".asJson
    }

  }

  sealed abstract class BallotTallier[A : Monoid] extends Tallier[StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId], A] {
    override def tallyAll(ballots: Iterable[StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]]): A = ballots.foldLeft(Monoid[A].empty) { case (sumSoFar, ballotWithFacts) => Monoid.combine(sumSoFar, tally(ballotWithFacts)) }
  }

  object BallotTallier {
    case object FormalBallots extends BallotTallier[Long] {
      override def tally(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Long =
        if (ballot.normalisedBallot.canonicalOrder.isDefined) 1 else 0
    }

    case object VotedAtl extends BallotTallier[Long] {
      override def tally(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Long =
        if (ballot.normalisedBallot.isNormalisedToAtl) 1 else 0
    }

    case object VotedAtlAndBtl extends BallotTallier[Long] {
      override def tally(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Long =
        if (ballot.normalisedBallot.atl.isSavedOrFormal && ballot.normalisedBallot.btl.isSavedOrFormal) 1 else 0
    }

    case object VotedBtl extends BallotTallier[Long] {
      override def tally(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Long =
        if (ballot.normalisedBallot.isNormalisedToBtl) 1 else 0
    }

    case object DonkeyVotes extends BallotTallier[Long] {
      override def tally(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Long =
        if (ballot.isDonkeyVote) 1 else 0
    }

    case object ExhaustedBallots extends BallotTallier[Long] {
      override def tally(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Long =
        ballot.exhaustion match {
          case _: Exhausted => 1
          case NotExhausted => 0
        }
    }

    case object ExhaustedVotes extends BallotTallier[Double] {
      override def tally(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Double =
        ballot.exhaustion match {
          case BallotExhaustion.Exhausted(_, value, _) => value.factor.toDouble
          case BallotExhaustion.NotExhausted => 0d
        }
    }

    case object UsedHowToVoteCard extends BallotTallier[Long] {
      override def tally(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Long =
        if (ballot.matchingHowToVote.isDefined) 1 else 0
    }

    case object Voted1Atl extends BallotTallier[Long] {
      override def tally(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Long =
        if (ballot.ballot.candidatePreferences.isEmpty && ballot.ballot.groupPreferences.size == 1 && (ballot.ballot.groupPreferences.head._2 == Preference.Numbered(1) || ballot.ballot.groupPreferences.head._2 == Preference.Tick || ballot.ballot.groupPreferences.head._2 == Preference.Cross)) 1 else 0
    }

    case object UsedSavingsProvision extends BallotTallier[Long] {
      override def tally(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Long =
        if (ballot.savingsProvisionsUsed.nonEmpty) 1 else 0
    }

    implicit def encoder[A]: Encoder[BallotTallier[A]] = {
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

}
