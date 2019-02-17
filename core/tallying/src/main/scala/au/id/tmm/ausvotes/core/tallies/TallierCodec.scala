package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.model.Codecs
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, DecodingFailure, Encoder, Json}

object TallierCodec {

  implicit val ballotCounterCodec: Codecs.Codec[BallotCounter] = Codecs.partialCodec[BallotCounter, String](
    encode = _.name,
    decode = {
      case BallotCounter.FormalBallots.name => BallotCounter.FormalBallots
      case BallotCounter.VotedAtl.name => BallotCounter.VotedAtl
      case BallotCounter.VotedAtlAndBtl.name => BallotCounter.VotedAtlAndBtl
      case BallotCounter.VotedBtl.name => BallotCounter.VotedBtl
      case BallotCounter.DonkeyVotes.name => BallotCounter.DonkeyVotes
      case BallotCounter.ExhaustedBallots.name => BallotCounter.ExhaustedBallots
      case BallotCounter.ExhaustedVotes.name => BallotCounter.ExhaustedVotes
      case BallotCounter.UsedHowToVoteCard.name => BallotCounter.UsedHowToVoteCard
      case BallotCounter.Voted1Atl.name => BallotCounter.Voted1Atl
      case BallotCounter.UsedSavingsProvision.name => BallotCounter.UsedSavingsProvision
    },
  )

  implicit val ballotGrouperEncoder: Encoder[BallotGrouper] = g =>
    Json.obj(
      "groups" -> (g match {
        case BallotGrouper0 => Json.arr()
        case BallotGrouper1(grouping1) => List(grouping1).asJson
        case BallotGrouper2(grouping1, grouping2) => List(grouping1, grouping2).asJson
        case BallotGrouper3(grouping1, grouping2, grouping3) => List(grouping1, grouping2, grouping3).asJson
        case BallotGrouper4(grouping1, grouping2, grouping3, grouping4) => List(grouping1, grouping2, grouping3, grouping4).asJson
      })
    )

  implicit val ballotGrouperDecoder: Decoder[BallotGrouper] = c =>
    c.get[List[BallotGrouping[Any]]]("groups").flatMap {
      case Nil => Right(BallotGrouper0)
      case grouping1 :: Nil => Right(BallotGrouper1(grouping1))
      case grouping1 :: grouping2 :: Nil => Right(BallotGrouper2(grouping1, grouping2))
      case grouping1 :: grouping2 :: grouping3 :: Nil => Right(BallotGrouper3(grouping1, grouping2, grouping3))
      case grouping1 :: grouping2 :: grouping3 :: grouping4 :: Nil => Right(BallotGrouper4(grouping1, grouping2, grouping3, grouping4))
      case _ => Left(DecodingFailure("Failed to decode ballot grouper", c.history))
    }

  implicit def ballotGroupingCodec: Codecs.Codec[BallotGrouping[Any]] = Codecs.partialCodec[BallotGrouping[Any], String](
    encode = _.name,
    decode = {
      case BallotGrouping.SenateElection.name => BallotGrouping.SenateElection.asInstanceOf[BallotGrouping[Any]]
      case BallotGrouping.State.name => BallotGrouping.State.asInstanceOf[BallotGrouping[Any]]
      case BallotGrouping.Division.name => BallotGrouping.Division.asInstanceOf[BallotGrouping[Any]]
      case BallotGrouping.VoteCollectionPoint.name => BallotGrouping.VoteCollectionPoint.asInstanceOf[BallotGrouping[Any]]
      case BallotGrouping.FirstPreferencedPartyNationalEquivalent.name => BallotGrouping.FirstPreferencedPartyNationalEquivalent.asInstanceOf[BallotGrouping[Any]]
      case BallotGrouping.FirstPreferencedParty.name => BallotGrouping.FirstPreferencedParty.asInstanceOf[BallotGrouping[Any]]
      case BallotGrouping.FirstPreferencedGroup.name => BallotGrouping.FirstPreferencedGroup.asInstanceOf[BallotGrouping[Any]]
      case BallotGrouping.UsedSavingsProvision.name => BallotGrouping.UsedSavingsProvision.asInstanceOf[BallotGrouping[Any]]
    },
  )

  implicit val encodeTallier: Encoder[Tallier] =
    Encoder.forProduct2[Tallier, Option[BallotGrouper], BallotCounter]("grouper", "counter") {
      case Tallier0(ballotCounter) => (None, ballotCounter)
      case Tallier1(ballotGrouper, ballotCounter) => (Some(ballotGrouper), ballotCounter)
      case Tallier2(ballotGrouper, ballotCounter) => (Some(ballotGrouper), ballotCounter)
      case Tallier3(ballotGrouper, ballotCounter) => (Some(ballotGrouper), ballotCounter)
      case Tallier4(ballotGrouper, ballotCounter) => (Some(ballotGrouper), ballotCounter)
    }

  implicit val decodeTallier: Decoder[Tallier] = c =>
    for {
      grouper <- c.get[BallotGrouper]("grouper")
      counter <- c.get[BallotCounter]("counter")
    } yield grouper match {
      case BallotGrouper0 => Tallier0(counter)
      case g @ BallotGrouper1(_) => Tallier1(g, counter)
      case g @ BallotGrouper2(_, _) => Tallier2(g, counter)
      case g @ BallotGrouper3(_, _, _) => Tallier3(g, counter)
      case g @ BallotGrouper4(_, _, _, _) => Tallier4(g, counter)
    }

}
