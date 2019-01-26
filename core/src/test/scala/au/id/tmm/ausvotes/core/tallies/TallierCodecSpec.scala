package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.model.computation.SavingsProvision
import au.id.tmm.ausvotes.core.tallies.TallierCodec._
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallotGroup, SenateElection}
import au.id.tmm.ausvotes.model.federal.{Division, FederalVcp}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class TallierCodecSpec extends ImprovedFlatSpec {

  behaviour of "the ballot counter encoder"

  it can "encode a ballot counter that counts FormalBallots" in {
    assert((BallotCounter.FormalBallots: BallotCounter).asJson === Json.fromString(BallotCounter.FormalBallots.name))
  }

  it can "encode a ballot counter that counts VotedAtl" in {
    assert((BallotCounter.VotedAtl: BallotCounter).asJson === Json.fromString(BallotCounter.VotedAtl.name))
  }

  it can "encode a ballot counter that counts VotedAtlAndBtl" in {
    assert((BallotCounter.VotedAtlAndBtl: BallotCounter).asJson === Json.fromString(BallotCounter.VotedAtlAndBtl.name))
  }

  it can "encode a ballot counter that counts VotedBtl" in {
    assert((BallotCounter.VotedBtl: BallotCounter).asJson === Json.fromString(BallotCounter.VotedBtl.name))
  }

  it can "encode a ballot counter that counts DonkeyVotes" in {
    assert((BallotCounter.DonkeyVotes: BallotCounter).asJson === Json.fromString(BallotCounter.DonkeyVotes.name))
  }

  it can "encode a ballot counter that counts ExhaustedBallots" in {
    assert((BallotCounter.ExhaustedBallots: BallotCounter).asJson === Json.fromString(BallotCounter.ExhaustedBallots.name))
  }

  it can "encode a ballot counter that counts ExhaustedVotes" in {
    assert((BallotCounter.ExhaustedVotes: BallotCounter).asJson === Json.fromString(BallotCounter.ExhaustedVotes.name))
  }

  it can "encode a ballot counter that counts UsedHowToVoteCard" in {
    assert((BallotCounter.UsedHowToVoteCard: BallotCounter).asJson === Json.fromString(BallotCounter.UsedHowToVoteCard.name))
  }

  it can "encode a ballot counter that counts Voted1Atl" in {
    assert((BallotCounter.Voted1Atl: BallotCounter).asJson === Json.fromString(BallotCounter.Voted1Atl.name))
  }

  it can "encode a ballot counter that counts UsedSavingsProvision" in {
    assert((BallotCounter.UsedSavingsProvision: BallotCounter).asJson === Json.fromString(BallotCounter.UsedSavingsProvision.name))
  }

  behaviour of "the ballot counter decoder"

  it can "decode a ballot counter that counts FormalBallots" in {
    assert(Json.fromString(BallotCounter.FormalBallots.name).as[BallotCounter] === Right(BallotCounter.FormalBallots))
  }

  it can "decode a ballot counter that counts VotedAtl" in {
    assert(Json.fromString(BallotCounter.VotedAtl.name).as[BallotCounter] === Right(BallotCounter.VotedAtl))
  }

  it can "decode a ballot counter that counts VotedAtlAndBtl" in {
    assert(Json.fromString(BallotCounter.VotedAtlAndBtl.name).as[BallotCounter] === Right(BallotCounter.VotedAtlAndBtl))
  }

  it can "decode a ballot counter that counts VotedBtl" in {
    assert(Json.fromString(BallotCounter.VotedBtl.name).as[BallotCounter] === Right(BallotCounter.VotedBtl))
  }

  it can "decode a ballot counter that counts DonkeyVotes" in {
    assert(Json.fromString(BallotCounter.DonkeyVotes.name).as[BallotCounter] === Right(BallotCounter.DonkeyVotes))
  }

  it can "decode a ballot counter that counts ExhaustedBallots" in {
    assert(Json.fromString(BallotCounter.ExhaustedBallots.name).as[BallotCounter] === Right(BallotCounter.ExhaustedBallots))
  }

  it can "decode a ballot counter that counts ExhaustedVotes" in {
    assert(Json.fromString(BallotCounter.ExhaustedVotes.name).as[BallotCounter] === Right(BallotCounter.ExhaustedVotes))
  }

  it can "decode a ballot counter that counts UsedHowToVoteCard" in {
    assert(Json.fromString(BallotCounter.UsedHowToVoteCard.name).as[BallotCounter] === Right(BallotCounter.UsedHowToVoteCard))
  }

  it can "decode a ballot counter that counts Voted1Atl" in {
    assert(Json.fromString(BallotCounter.Voted1Atl.name).as[BallotCounter] === Right(BallotCounter.Voted1Atl))
  }

  it can "decode a ballot counter that counts UsedSavingsProvision" in {
    assert(Json.fromString(BallotCounter.UsedSavingsProvision.name).as[BallotCounter] === Right(BallotCounter.UsedSavingsProvision))
  }

  behaviour of "the ballot grouping encoder"

  it can "encode a ballot grouping by SenateElection" in {
    assert(BallotGrouping.SenateElection.asInstanceOf[BallotGrouping[Any]].asJson === Json.fromString(BallotGrouping.SenateElection.name))
  }

  it can "encode a ballot grouping by State" in {
    assert(BallotGrouping.State.asInstanceOf[BallotGrouping[Any]].asJson === Json.fromString(BallotGrouping.State.name))
  }

  it can "encode a ballot grouping by Division" in {
    assert(BallotGrouping.Division.asInstanceOf[BallotGrouping[Any]].asJson === Json.fromString(BallotGrouping.Division.name))
  }

  it can "encode a ballot grouping by VoteCollectionPoint" in {
    assert(BallotGrouping.VoteCollectionPoint.asInstanceOf[BallotGrouping[Any]].asJson === Json.fromString(BallotGrouping.VoteCollectionPoint.name))
  }

  it can "encode a ballot grouping by FirstPreferencedPartyNationalEquivalent" in {
    assert(BallotGrouping.FirstPreferencedPartyNationalEquivalent.asInstanceOf[BallotGrouping[Any]].asJson === Json.fromString(BallotGrouping.FirstPreferencedPartyNationalEquivalent.name))
  }

  it can "encode a ballot grouping by FirstPreferencedParty" in {
    assert(BallotGrouping.FirstPreferencedParty.asInstanceOf[BallotGrouping[Any]].asJson === Json.fromString(BallotGrouping.FirstPreferencedParty.name))
  }

  it can "encode a ballot grouping by FirstPreferencedGroup" in {
    assert(BallotGrouping.FirstPreferencedGroup.asInstanceOf[BallotGrouping[Any]].asJson === Json.fromString(BallotGrouping.FirstPreferencedGroup.name))
  }

  it can "encode a ballot grouping by UsedSavingsProvision" in {
    assert(BallotGrouping.UsedSavingsProvision.asInstanceOf[BallotGrouping[Any]].asJson === Json.fromString(BallotGrouping.UsedSavingsProvision.name))
  }

  behaviour of "the ballot grouping decoder"

  it can "decoder a ballot grouping by SenateElection" in {
    assert(Json.fromString(BallotGrouping.SenateElection.name).as[BallotGrouping[Any]] === Right(BallotGrouping.SenateElection: BallotGrouping[SenateElection]))
  }

  it can "decoder a ballot grouping by State" in {
    assert(Json.fromString(BallotGrouping.State.name).as[BallotGrouping[Any]] === Right(BallotGrouping.State: BallotGrouping[State]))
  }

  it can "decoder a ballot grouping by Division" in {
    assert(Json.fromString(BallotGrouping.Division.name).as[BallotGrouping[Any]] === Right(BallotGrouping.Division: BallotGrouping[Division]))
  }

  it can "decoder a ballot grouping by VoteCollectionPoint" in {
    assert(Json.fromString(BallotGrouping.VoteCollectionPoint.name).as[BallotGrouping[Any]] === Right(BallotGrouping.VoteCollectionPoint: BallotGrouping[FederalVcp]))
  }

  it can "decoder a ballot grouping by FirstPreferencedPartyNationalEquivalent" in {
    assert(Json.fromString(BallotGrouping.FirstPreferencedPartyNationalEquivalent.name).as[BallotGrouping[Any]] === Right(BallotGrouping.FirstPreferencedPartyNationalEquivalent: BallotGrouping[Option[Party]]))
  }

  it can "decoder a ballot grouping by FirstPreferencedParty" in {
    assert(Json.fromString(BallotGrouping.FirstPreferencedParty.name).as[BallotGrouping[Any]] === Right(BallotGrouping.FirstPreferencedParty: BallotGrouping[Option[Party]]))
  }

  it can "decoder a ballot grouping by FirstPreferencedGroup" in {
    assert(Json.fromString(BallotGrouping.FirstPreferencedGroup.name).as[BallotGrouping[Any]] === Right(BallotGrouping.FirstPreferencedGroup: BallotGrouping[SenateBallotGroup]))
  }

  it can "decoder a ballot grouping by UsedSavingsProvision" in {
    assert(Json.fromString(BallotGrouping.UsedSavingsProvision.name).as[BallotGrouping[Any]] === Right(BallotGrouping.UsedSavingsProvision: BallotGrouping[SavingsProvision]))
  }


}
