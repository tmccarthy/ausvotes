package au.id.tmm.ausvotes.core.tallies2

import au.id.tmm.ausvotes.core.computations.BallotWithFacts
import au.id.tmm.ausvotes.core.computations.parties.PartyEquivalenceComputation
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion.{Exhausted, NotExhausted}
import au.id.tmm.ausvotes.core.model.computation.{BallotExhaustion, SavingsProvision}
import au.id.tmm.ausvotes.core.tallies2.typeclasses.{Grouper, Tallier}
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallotGroup, SenateElection}
import au.id.tmm.ausvotes.model.federal.{Division, FederalVcp}
import au.id.tmm.ausvotes.model.instances.BallotNormalisationResultInstances.Ops
import au.id.tmm.countstv.normalisation.Preference
import au.id.tmm.utilities.geo.australia.State
import cats.Monoid
import cats.instances.double._
import cats.instances.long._
import io.circe.Encoder
import io.circe.syntax.EncoderOps

object SenateElectionTalliers {

  sealed trait BallotGrouping[G]

  object BallotGrouping {
    case object SenateElection extends BallotGrouping[SenateElection]
    case object State extends BallotGrouping[State]
    case object Division extends BallotGrouping[Division]
    case object VoteCollectionPoint extends BallotGrouping[FederalVcp]
    case object FirstPreferencedPartyNationalEquivalent extends BallotGrouping[Option[Party]]
    case object FirstPreferencedParty extends BallotGrouping[Option[Party]]
    case object FirstPreferencedGroup extends BallotGrouping[SenateBallotGroup]
    case object UsedSavingsProvision extends BallotGrouping[SavingsProvision]

    implicit val encoder: Encoder[BallotGrouping[_]] = {
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

  implicit def senateElectionBallotGroupingIsABallotGrouping[G]: Grouper[BallotGrouping[G], G, BallotWithFacts] =
    new Grouper[BallotGrouping[G], G, BallotWithFacts] {
      override def groupsOf(grouper: BallotGrouping[G])(ballot: BallotWithFacts): Set[G] = grouper match {
        case BallotGrouping.SenateElection => Set(ballot.ballot.election.election)
        case BallotGrouping.State => Set(ballot.ballot.jurisdiction.state)
        case BallotGrouping.Division => Set(ballot.ballot.jurisdiction.electorate)
        case BallotGrouping.VoteCollectionPoint => Set(ballot.ballot.jurisdiction.voteCollectionPoint)
        case BallotGrouping.FirstPreferencedPartyNationalEquivalent => Set(ballot.firstPreference.party.map(PartyEquivalenceComputation.nationalEquivalentOf))
        case BallotGrouping.FirstPreferencedParty => Set(ballot.firstPreference.party)
        case BallotGrouping.FirstPreferencedGroup => Set(ballot.firstPreference.group)
        case BallotGrouping.UsedSavingsProvision => ballot.savingsProvisionsUsed
      }
    }

  sealed abstract class BallotTallier[A : Monoid]

  object BallotTallier {
    case object FormalBallots extends BallotTallier[Long]
    case object VotedAtl extends BallotTallier[Long]
    case object VotedAtlAndBtl extends BallotTallier[Long]
    case object VotedBtl extends BallotTallier[Long]
    case object DonkeyVotes extends BallotTallier[Long]
    case object ExhaustedBallots extends BallotTallier[Long]
    case object ExhaustedVotes extends BallotTallier[Double]
    case object UsedHowToVoteCard extends BallotTallier[Long]
    case object Voted1Atl extends BallotTallier[Long]
    case object UsedSavingsProvision extends BallotTallier[Long]

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

  implicit def senateElectionBallotTallierIsATallier[A : Monoid]: Tallier[BallotTallier[A], BallotWithFacts, A] =
    new Tallier[BallotTallier[A], BallotWithFacts, A] {
      override def tallyAll(t: BallotTallier[A])(ballots: Iterable[BallotWithFacts]): A =
        ballots.foldLeft(Monoid[A].empty) {
          case (sumSoFar, ballotWithFacts) => Monoid[A].combine(sumSoFar, tally(t)(ballotWithFacts))
        }

      override def tally(t: BallotTallier[A])(ballot: BallotWithFacts): A = t match {
        case BallotTallier.FormalBallots => if (ballot.normalisedBallot.canonicalOrder.isDefined) 1 else 0
        case BallotTallier.VotedAtl => if (ballot.normalisedBallot.isNormalisedToAtl) 1 else 0
        case BallotTallier.VotedAtlAndBtl => if (ballot.normalisedBallot.atl.isSavedOrFormal && ballot.normalisedBallot.btl.isSavedOrFormal) 1 else 0
        case BallotTallier.VotedBtl => if (ballot.normalisedBallot.isNormalisedToBtl) 1 else 0
        case BallotTallier.DonkeyVotes => if (ballot.isDonkeyVote) 1 else 0
        case BallotTallier.ExhaustedBallots => ballot.exhaustion match {
          case _: Exhausted => 1
          case NotExhausted => 0
        }
        case BallotTallier.ExhaustedVotes => ballot.exhaustion match {
          case BallotExhaustion.Exhausted(_, value, _) => value.factor.toDouble
          case BallotExhaustion.NotExhausted => 0d
        }
        case BallotTallier.UsedHowToVoteCard => if (ballot.matchingHowToVote.isDefined) 1 else 0
        case BallotTallier.Voted1Atl => if (ballot.ballot.candidatePreferences.isEmpty && ballot.ballot.groupPreferences.size == 1 && (ballot.ballot.groupPreferences.head._2 == Preference.Numbered(1) || ballot.ballot.groupPreferences.head._2 == Preference.Tick || ballot.ballot.groupPreferences.head._2 == Preference.Cross)) 1 else 0
        case BallotTallier.UsedSavingsProvision => if (ballot.savingsProvisionsUsed.nonEmpty) 1 else 0
      }
    }

}
