package au.id.tmm.ausvotes.shared.recountresources

import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.codecs.CandidateCodec._
import au.id.tmm.ausvotes.core.model.codecs.DupelessSeqCodec._
import au.id.tmm.ausvotes.core.model.codecs.GeneralCodecs._
import au.id.tmm.ausvotes.core.model.codecs.PartyCodec._
import au.id.tmm.ausvotes.core.model.codecs.ProbabilityMeasureCodec._
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.countstv.model.{CandidateStatuses, CompletedCount, VoteCount}
import au.id.tmm.utilities.collection.DupelessSeq
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure

final case class CountSummary(
                               request: CountSummary.Request,
                               outcomePossibilities: ProbabilityMeasure[CountSummary.Outcome],
                             )

object CountSummary {

  def from(
            request: RecountRequest,
            completedCountPossibilities: ProbabilityMeasure[CompletedCount[Candidate]],
          ): Either[CountSummaryConstructionError, CountSummary] =
    for {

      ineligibleCandidates <- completedCountPossibilities.map(_.outcomes.ineligibleCandidates).onlyOutcome match {
        case Some(ineligibleCandidates) => Right(ineligibleCandidates)
        case None => Left(CountSummaryConstructionError.MultipleIneligibleCandidatePossibilities())
      }

      _ <- if (ineligibleCandidates.map(_.aecId) != request.ineligibleCandidateAecIds) Left(CountSummaryConstructionError.RequestedIneligibleCandidatesMismatch()) else Right(Unit)

    } yield CountSummary(
      CountSummary.Request(
        request.election,
        request.state,
        request.vacancies,
        ineligibleCandidates,
        doRounding = request.doRounding,
      ),
      outcomePossibilities = completedCountPossibilities.map { completedCount =>
        CountSummary.Outcome(
          completedCount.outcomes.electedCandidates,
          completedCount.countSteps.last.candidateVoteCounts.exhausted,
          completedCount.countSteps.last.candidateVoteCounts.roundingError,
          completedCount.outcomes,
        )
      },
    )

  abstract class CountSummaryConstructionError extends ExceptionCaseClass

  object CountSummaryConstructionError {
    final case class MultipleIneligibleCandidatePossibilities() extends CountSummaryConstructionError
    final case class RequestedIneligibleCandidatesMismatch() extends CountSummaryConstructionError
  }

  final case class Request(
                            election: SenateElection,
                            state: State,
                            numVacancies: Int,
                            ineligibleCandidates: Set[Candidate],
                            doRounding: Boolean,
                          )

  final case class Outcome(
                            elected: DupelessSeq[Candidate],
                            exhaustedVotes: VoteCount,
                            roundingError: VoteCount,
                            candidateOutcomes: CandidateStatuses[Candidate],
                          )

  object Request {
    implicit val encode: EncodeJson[Request] = request =>
      jObjectFields(
        "election" -> request.election.asJson,
        "state" -> request.state.asJson,
        "numVacancies" -> request.numVacancies.asJson,
        "ineligibleCandidates" -> request.ineligibleCandidates.asJson,
        "doRounding" -> request.doRounding.asJson,
      )

    implicit def decode(implicit decodeCandidate: DecodeJson[Candidate]): DecodeJson[Request] =
      jdecode5L(Request.apply)("election", "state", "numVacancies", "ineligibleCandidates", "doRounding")
  }

  object Outcome {

    implicit val encode: EncodeJson[Outcome] = outcome =>
      jObjectFields(
        "elected" -> outcome.candidateOutcomes.electedCandidates.asJson,
        "exhaustedVotes" -> outcome.exhaustedVotes.asJson,
        "roundingError" -> outcome.roundingError.asJson,
        "candidateOutcomes" -> outcome.candidateOutcomes.asJson,
      )

    implicit def decode(implicit decodeCandidate: DecodeJson[Candidate]): DecodeJson[Outcome] =
      jdecode4L(Outcome.apply)("elected", "exhaustedVotes", "roundingError", "candidateOutcomes")

  }

  implicit val encodeRecountResult: EncodeJson[CountSummary] = result =>
    jObjectFields(
      "request" -> result.request.asJson,
      "outcomePossibilities" -> result.outcomePossibilities.asJson,
    )

  implicit def decodeRecountResult(implicit decodeCandidates: DecodeJson[Candidate]): DecodeJson[CountSummary] =
    jdecode2L(CountSummary.apply)("request", "outcomePossibilities")

}
