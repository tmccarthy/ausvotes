package au.id.tmm.ausvotes.model.instances

import au.id.tmm.ausvotes.model.Codecs
import au.id.tmm.ausvotes.model.Codecs.Codec
import au.id.tmm.countstv.model.values.{Count, NumPapers, NumVotes, Ordinal}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateStatuses, VoteCount}
import au.id.tmm.countstv.normalisation.Preference
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, DecodingFailure, Encoder, Json}

object CountStvCodecs {

  implicit val countCodec: Codec[Count] = Codecs.simpleCodec(_.asInt,Count(_))

  implicit val ordinalCodec: Codec[Ordinal] = Codecs.simpleCodec(_.asInt,Ordinal(_))

  implicit val numPapersCodec: Codec[NumPapers] = Codecs.simpleCodec(_.asLong,NumPapers(_))

  implicit val numVotesCodec: Codec[NumVotes] = Codecs.simpleCodec(_.asDouble,NumVotes(_))

  implicit val voteCountEncoder: Encoder[VoteCount] = Encoder.forProduct2("papers", "votes")(c => (c.numPapers, c.numVotes))
  implicit val voteCountDecoder: Decoder[VoteCount] = Decoder.forProduct2("papers", "votes")(VoteCount.apply)

  implicit val encodeCandidateStatus: Encoder[CandidateStatus] = {
    case CandidateStatus.Elected(ordinalElected, electedAtCount) =>
      Json.obj(
        "status" -> Json.fromString("elected"),
        "ordinal" -> ordinalElected.asJson,
        "count" -> electedAtCount.asJson,
      )

    case CandidateStatus.Excluded(ordinalExcluded, excludedAtCount) =>
      Json.obj(
        "status" -> Json.fromString("excluded"),
        "ordinal" -> ordinalExcluded.asJson,
        "count" -> excludedAtCount.asJson,
      )
    case CandidateStatus.Remaining => Json.obj("status" -> Json.fromString("remaining"))
    case CandidateStatus.Ineligible => Json.obj("status" -> Json.fromString("ineligible"))
  }

  implicit val decodeCandidateStatus: Decoder[CandidateStatus] = Decoder { c =>
    c.get[String]("status").flatMap {
      case "elected" =>
        for {
          ordinalElected <- c.get[Int]("ordinal")
          electedAtCount <- c.get[Int]("count")
        } yield CandidateStatus.Elected(Ordinal(ordinalElected), Count(electedAtCount))
      case "excluded" =>
        for {
          ordinalExcluded <- c.get[Int]("ordinal")
          excludedAtCount <- c.get[Int]("count")
        } yield CandidateStatus.Excluded(Ordinal(ordinalExcluded), Count(excludedAtCount))
      case "remaining" => Right(CandidateStatus.Remaining)
      case "ineligible" => Right(CandidateStatus.Ineligible)
      case invalid => Left(DecodingFailure(s"""Invalid candidate status "$invalid"""", c.history))
    }
  }

  implicit def encodeCandidateStatuses[C : Encoder]: Encoder[CandidateStatuses[C]] = candidateStatuses => {
    Json.arr(
      candidateStatuses.asMap.toList
        .sortBy { case (candidate, candidateStatus) => candidateStatus }(statusOrdering)
        .map { case (candidate, candidateStatus) =>
          Json.obj(
            "candidate" -> candidate.asJson,
            "outcome" -> candidateStatus.asJson,
          )
        }: _*
    )
  }

  private val statusOrdering: Ordering[CandidateStatus] = (left, right) => {
    import CandidateStatus._

    (left, right) match {
      case (Elected(leftOrdinal, _), Elected(rightOrdinal, _)) => Ordinal.ordering.compare(leftOrdinal, rightOrdinal)
      case (_: Elected, _) => -1
      case (_, _: Elected) => 1
      case (Remaining, Remaining) => 0
      case (Remaining, _) => -1
      case (_, Remaining) => 1
      case (Excluded(leftOrdinal, _), Excluded(rightOrdinal, _)) => Ordinal.ordering.reverse.compare(leftOrdinal, rightOrdinal)
      case (_: Excluded, _) => -1
      case (_, _: Excluded) => 1
      case (Ineligible, Ineligible) => 0
    }
  }

  implicit def decodeCandidateStatuses[C : Decoder]: Decoder[CandidateStatuses[C]] = c =>
    c.as[List[(C, CandidateStatus)]](Decoder.decodeList(decodeCandidateStatusesElement)).map { statusElements =>
      CandidateStatuses[C](
        statusElements
          .toMap
      )
    }

  private def decodeCandidateStatusesElement[C : Decoder]: Decoder[(C, CandidateStatus)] = c =>
    for {
      candidate <- c.get[C]("candidate")
      outcome <- c.get[CandidateStatus]("outcome")
    } yield candidate -> outcome

  implicit val preferenceEncoder: Encoder[Preference] = {
    case Preference.Numbered(asInt) => Json.fromInt(asInt)
    case Preference.Tick => Json.fromString("✓")
    case Preference.Cross => Json.fromString("x")
  }

  implicit val preferenceDecoder: Decoder[Preference] =
    Decoder.decodeInt.map[Preference](Preference.Numbered) or
      Decoder.decodeString.emap[Preference] {
        case "✓" => Right(Preference.Tick)
        case "x" => Right(Preference.Cross)
        case x => Left(s"""Invalid preference "$x"""")
      }

}
