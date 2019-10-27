package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.model.computation.SavingsProvision
import au.id.tmm.ausvotes.core.tallies.SenateElectionTalliers.{BallotGrouping, BallotTallier}
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallotGroup, SenateElection}
import au.id.tmm.ausvotes.model.federal.{Division, FederalVcp}
import au.id.tmm.ausgeo.State
import org.scalatest.FlatSpec
import io.circe.syntax.EncoderOps

class SenateElectionTalliersSpec extends FlatSpec {

  behavior of "the ballot counter encoder"

  it can "encode a ballot counter that counts FormalBallots" in {
    assert((BallotTallier.FormalBallots: BallotTallier[Long]).asJson === "formal_ballots".asJson)
  }

  it can "encode a ballot counter that counts VotedAtl" in {
    assert((BallotTallier.VotedAtl: BallotTallier[Long]).asJson === "voted_atl".asJson)
  }

  it can "encode a ballot counter that counts VotedAtlAndBtl" in {
    assert((BallotTallier.VotedAtlAndBtl: BallotTallier[Long]).asJson === "voted_atl_and_btl".asJson)
  }

  it can "encode a ballot counter that counts VotedBtl" in {
    assert((BallotTallier.VotedBtl: BallotTallier[Long]).asJson === "voted_btl".asJson)
  }

  it can "encode a ballot counter that counts DonkeyVotes" in {
    assert((BallotTallier.DonkeyVotes: BallotTallier[Long]).asJson === "donkey_votes".asJson)
  }

  it can "encode a ballot counter that counts ExhaustedBallots" in {
    assert((BallotTallier.ExhaustedBallots: BallotTallier[Long]).asJson === "exhausted_ballots".asJson)
  }

  it can "encode a ballot counter that counts ExhaustedVotes" in {
    assert((BallotTallier.ExhaustedVotes: BallotTallier[Double]).asJson === "exhausted_votes".asJson)
  }

  it can "encode a ballot counter that counts UsedHowToVoteCard" in {
    assert((BallotTallier.UsedHowToVoteCard: BallotTallier[Long]).asJson === "used_how_to_vote_card".asJson)
  }

  it can "encode a ballot counter that counts Voted1Atl" in {
    assert((BallotTallier.Voted1Atl: BallotTallier[Long]).asJson === "voted_1_atl".asJson)
  }

  it can "encode a ballot counter that counts UsedSavingsProvision" in {
    assert((BallotTallier.UsedSavingsProvision: BallotTallier[Long]).asJson === "used_savings_provision".asJson)
  }

  behavior of "the ballot grouping encoder"

  it can "encode a ballot grouping by SenateElection" in {
    assert((BallotGrouping.SenateElection: BallotGrouping[SenateElection]).asJson === "senate_election".asJson)
  }

  it can "encode a ballot grouping by State" in {
    assert((BallotGrouping.State: BallotGrouping[State]).asJson === "state".asJson)
  }

  it can "encode a ballot grouping by Division" in {
    assert((BallotGrouping.Division: BallotGrouping[Division]).asJson === "division".asJson)
  }

  it can "encode a ballot grouping by VoteCollectionPoint" in {
    assert((BallotGrouping.VoteCollectionPoint: BallotGrouping[FederalVcp]).asJson === "vote_collection_point".asJson)
  }

  it can "encode a ballot grouping by FirstPreferencedPartyNationalEquivalent" in {
    assert((BallotGrouping.FirstPreferencedPartyNationalEquivalent: BallotGrouping[Option[Party]]).asJson === "first_preferenced_party_national_equivalent".asJson)
  }

  it can "encode a ballot grouping by FirstPreferencedParty" in {
    assert((BallotGrouping.FirstPreferencedParty: BallotGrouping[Option[Party]]).asJson === "first_preferenced_party".asJson)
  }

  it can "encode a ballot grouping by FirstPreferencedGroup" in {
    assert((BallotGrouping.FirstPreferencedGroup: BallotGrouping[SenateBallotGroup]).asJson === "first_preferenced_group".asJson)
  }

  it can "encode a ballot grouping by UsedSavingsProvision" in {
    assert((BallotGrouping.UsedSavingsProvision: BallotGrouping[SavingsProvision]).asJson === "used_savings_provision".asJson)
  }

}
