package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Name
import au.id.tmm.countstv.model.values.{Count, NumPapers, NumVotes, Ordinal}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateStatuses, VoteCount}
import au.id.tmm.utilities.geo.australia.State

import scala.reflect.ClassTag

object GeneralCodecs {

  private def codecUsing[A : ClassTag](encode: A => String, decode: String => Option[A]): CodecJson[A] = CodecJson[A](
    encoder = a => jString(encode(a)),
    decoder = cursor => cursor.as[String].flatMap { json =>
      val resolvedEntity = decode(json)

      resolvedEntity match {
        case Some(election) => DecodeResult.ok(election)
        case None => DecodeResult.fail(s"Could not resolve ${implicitly[ClassTag[A]].runtimeClass.getSimpleName} '$json'", cursor.history)
      }
    }
  )

  implicit val senateElectionCodec: CodecJson[SenateElection] = codecUsing(encode = _.id, decode = SenateElection.forId)

  implicit val stateCodec: CodecJson[State] = codecUsing(encode = _.abbreviation, decode = State.fromAbbreviation)

  implicit val nameCodec: CodecJson[Name] = casecodec2(Name.apply, Name.unapply)("givenNames", "surname")

  implicit val countCodec: CodecJson[Count] = CodecJson(
    encoder = _.asInt.asJson,
    decoder = c => c.as[Int].map(Count(_)),
  )

  implicit val ordinalCodec: CodecJson[Ordinal] = CodecJson(
    encoder = _.asInt.asJson,
    decoder = c => c.as[Int].map(Ordinal(_)),
  )

  implicit val numPapersCodec: CodecJson[NumPapers] = CodecJson(
    encoder = _.asLong.asJson,
    decoder = c => c.as[Long].map(NumPapers(_)),
  )

  implicit val numVotesCodec: CodecJson[NumVotes] = CodecJson(
    encoder = _.asLong.asJson,
    decoder = c => c.as[Long].map(NumVotes(_)),
  )

  implicit val voteCountCodec: CodecJson[VoteCount] = casecodec2(VoteCount.apply, VoteCount.unapply)("papers", "votes")

  implicit val encodeCandidateStatus: EncodeJson[CandidateStatus] = {
    case CandidateStatus.Elected(ordinalElected, electedAtCount) =>
      jObjectFields(
        "status" -> jString("elected"),
        "ordinal" -> ordinalElected.asJson,
        "count" -> electedAtCount.asJson,
      )

    case CandidateStatus.Excluded(ordinalExcluded, excludedAtCount) =>
      jObjectFields(
        "status" -> jString("excluded"),
        "ordinal" -> ordinalExcluded.asJson,
        "count" -> excludedAtCount.asJson,
      )
    case CandidateStatus.Remaining => jObjectFields("status" -> jString("remaining"))
    case CandidateStatus.Ineligible => jObjectFields("status" -> jString("ineligible"))
  }

  implicit val decodeCandidateStatus: DecodeJson[CandidateStatus] = DecodeJson { c =>
    c.downField("status").as[String].flatMap {
      case "elected" =>
        for {
          ordinalElected <- c.downField("ordinal").as[Int]
          electedAtCount <- c.downField("count").as[Int]
        } yield CandidateStatus.Elected(Ordinal(ordinalElected), Count(electedAtCount))
      case "excluded" =>
        for {
          ordinalExcluded <- c.downField("ordinal").as[Int]
          excludedAtCount <- c.downField("count").as[Int]
        } yield CandidateStatus.Excluded(Ordinal(ordinalExcluded), Count(excludedAtCount))
      case "remaining" => DecodeResult.ok(CandidateStatus.Remaining)
      case "ineligible" => DecodeResult.ok(CandidateStatus.Ineligible)
      case invalid => DecodeResult.fail(s"""Invalid candidate status "$invalid"""", c.history)
    }
  }

  implicit def encodeCandidateStatuses[C : EncodeJson]: EncodeJson[CandidateStatuses[C]] = candidateStatuses => {
    jArrayElements(
      candidateStatuses.asMap.toList
        .sortBy { case (candidate, candidateStatus) => candidateStatus }(statusOrdering)
        .map { case (candidate, candidateStatus) =>
          jObjectFields(
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

  implicit def decodeCandidateStatuses[C : DecodeJson]: DecodeJson[CandidateStatuses[C]] = c =>
    c.as[List[(C, CandidateStatus)]](ListDecodeJson(decodeCandidateStatusesElement)).map { statusElements =>
      CandidateStatuses[C](
        statusElements
          .toMap
      )
    }

  private def decodeCandidateStatusesElement[C : DecodeJson]: DecodeJson[(C, CandidateStatus)] = c =>
    for {
      candidate <- c.downField("candidate").as[C]
      outcome <- c.downField("outcome").as[CandidateStatus]
    } yield candidate -> outcome

}
