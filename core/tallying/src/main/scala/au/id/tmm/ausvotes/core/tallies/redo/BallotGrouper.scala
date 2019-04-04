package au.id.tmm.ausvotes.core.tallies.redo

import au.id.tmm.ausvotes.core.computations.SenateBallotWithFacts
import au.id.tmm.ausvotes.core.computations.parties.PartyEquivalenceComputation
import au.id.tmm.ausvotes.core.model.computation.SavingsProvision
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.{Division, FederalVcp}
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallotGroup, SenateElection}
import au.id.tmm.utilities.geo.australia.State
import io.circe.Encoder
import io.circe.syntax.EncoderOps

sealed trait BallotGrouper[-B, G] {
  def groupsOf(ballot: B): Set[G]
}

object BallotGrouper {
  sealed trait SenateBallotGrouper[G] extends BallotGrouper[SenateBallotWithFacts, G]

  object SenateBallotGrouper {
    case object SenateElection extends SenateBallotGrouper[SenateElection] {
      override def groupsOf(ballot: SenateBallotWithFacts): Set[SenateElection] = Set(ballot.ballot.election.election)
    }

    case object State extends SenateBallotGrouper[State] {
      override def groupsOf(ballot: SenateBallotWithFacts): Set[State] = Set(ballot.ballot.jurisdiction.state)
    }

    case object Division extends SenateBallotGrouper[Division] {
      override def groupsOf(ballot: SenateBallotWithFacts): Set[Division] = Set(ballot.ballot.jurisdiction.electorate)
    }

    case object VoteCollectionPoint extends SenateBallotGrouper[FederalVcp] {
      override def groupsOf(ballot: SenateBallotWithFacts): Set[FederalVcp] = Set(ballot.ballot.jurisdiction.voteCollectionPoint)
    }

    case object FirstPreferencedPartyNationalEquivalent extends SenateBallotGrouper[Option[Party]] {
      override def groupsOf(ballot: SenateBallotWithFacts): Set[Option[Party]] = Set(ballot.firstPreference.party.map(PartyEquivalenceComputation.nationalEquivalentOf))
    }

    case object FirstPreferencedParty extends SenateBallotGrouper[Option[Party]] {
      override def groupsOf(ballot: SenateBallotWithFacts): Set[Option[Party]] = Set(ballot.firstPreference.party)
    }

    case object FirstPreferencedGroup extends SenateBallotGrouper[SenateBallotGroup] {
      override def groupsOf(ballot: SenateBallotWithFacts): Set[SenateBallotGroup] = Set(ballot.firstPreference.group)
    }

    case object UsedSavingsProvision extends SenateBallotGrouper[SavingsProvision] {
      override def groupsOf(ballot: SenateBallotWithFacts): Set[SavingsProvision] = ballot.savingsProvisionsUsed
    }

    implicit def encoder[G]: Encoder[SenateBallotGrouper[G]] = {
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

  implicit def encoder[B, G]: Encoder[BallotGrouper[B, G]] = {
    case g: SenateBallotGrouper[G] => SenateBallotGrouper.encoder.apply(g)
  }
}
