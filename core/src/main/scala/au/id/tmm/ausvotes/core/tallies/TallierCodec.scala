package au.id.tmm.ausvotes.core.tallies

import io.circe.Encoder

object TallierCodec {

  implicit val encodeBallotCounter: Encoder[BallotCounter] = ???

  implicit val encodeBallotGrouper: Encoder[BallotGrouper] = ???

  implicit val encodeTallier: Encoder[Tallier] =
    Encoder.forProduct2[Tallier, Option[BallotGrouper], BallotCounter]("grouper", "counter") {
      case Tallier0(ballotCounter) => (None, ballotCounter)
      case Tallier1(ballotGrouper, ballotCounter) => (Some(ballotGrouper), ballotCounter)
      case Tallier2(ballotGrouper, ballotCounter) => (Some(ballotGrouper), ballotCounter)
      case Tallier3(ballotGrouper, ballotCounter) => (Some(ballotGrouper), ballotCounter)
      case Tallier4(ballotGrouper, ballotCounter) => (Some(ballotGrouper), ballotCounter)
    }

}
