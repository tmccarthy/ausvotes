package au.id.tmm.ausvotes.api.model.recount

import argonaut.Argonaut._
import argonaut.EncodeJson
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.codecs.GeneralCodecs._
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.countstv.model.values.{Count, Ordinal}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateStatuses}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import spire.math.Rational

final case class RecountResult(
                                election: SenateElection,
                                state: State,
                                numVacancies: Int,
                                ineligibleCandidates: Set[Candidate],
                                candidateOutcomeProbabilities: ProbabilityMeasure[CandidateStatuses[Candidate]]
                              )

object RecountResult {
  // TODO these codecs will need to be moved somewhere shared

  implicit val encodeRational: EncodeJson[Rational] = r => jString(s"${r.numerator}/${r.denominator}")

  implicit def encodeProbabilityMeasure[A : EncodeJson]: EncodeJson[ProbabilityMeasure[A]] = probabilityMeasure => {
    jArrayElements(
      probabilityMeasure.asMap.toStream.map { case (possibility, probability) =>
        jObjectFields(
          "probability" -> probability.asJson,
          "outcome" -> possibility.asJson,
        )
      }: _*
    )
  }

  implicit val encodeOrdinal: EncodeJson[Ordinal] = ordinal => jNumber(ordinal.asInt)

  implicit val encodeCount: EncodeJson[Count] = count => jNumber(count.asInt)

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

  implicit def encodeCandidateStatuses[A : EncodeJson]: EncodeJson[CandidateStatuses[A]] = candidateStatuses => {
    val statusOrdering: Ordering[CandidateStatus] = (left, right) => {
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

  implicit def encodeRecountResult(implicit encodeCandidate: EncodeJson[Candidate]): EncodeJson[RecountResult] = result =>
    jObjectFields(
      "election" -> result.election.asJson,
      "state" -> result.state.asJson,
      "numVacancies" -> result.numVacancies.asJson,
      "ineligibleCandidates" -> result.ineligibleCandidates.asJson,
      "outcomePossibilities" -> result.candidateOutcomeProbabilities.asJson,
    )
}
