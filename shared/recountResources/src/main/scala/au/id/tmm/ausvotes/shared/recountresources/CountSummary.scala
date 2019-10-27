package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateElectionForState}
import au.id.tmm.ausvotes.model.instances.CountStvCodecs._
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.countstv.model.{CandidateStatuses, CompletedCount, VoteCount}
import au.id.tmm.utilities.collection.DupelessSeq
import au.id.tmm.probability.measure.codecs._
import au.id.tmm.probability.measure.ProbabilityMeasure
import io.circe.{Decoder, Encoder}

final case class CountSummary(
                               request: CountSummary.Request,
                               outcomePossibilities: ProbabilityMeasure[CountSummary.Outcome],
                             )

object CountSummary {

  def from(
            request: RecountRequest,
            completedCountPossibilities: ProbabilityMeasure[CompletedCount[SenateCandidate]],
          ): Either[CountSummaryConstructionError, CountSummary] =
    for {

      ineligibleCandidates <- completedCountPossibilities.map(_.outcomes.ineligibleCandidates).onlyOutcome match {
        case Some(ineligibleCandidates) => Right(ineligibleCandidates)
        case None => Left(CountSummaryConstructionError.MultipleIneligibleCandidatePossibilities())
      }

      _ <- if (ineligibleCandidates.map(_.candidateDetails.id) != request.ineligibleCandidateAecIds) Left(CountSummaryConstructionError.RequestedIneligibleCandidatesMismatch()) else Right(())

    } yield CountSummary(
      CountSummary.Request(
        request.election,
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
                            election: SenateElectionForState,
                            numVacancies: Int,
                            ineligibleCandidates: Set[SenateCandidate],
                            doRounding: Boolean,
                          )

  final case class Outcome(
                            elected: DupelessSeq[SenateCandidate],
                            exhaustedVotes: VoteCount,
                            roundingError: VoteCount,
                            candidateOutcomes: CandidateStatuses[SenateCandidate],
                          )

  object Request {
    implicit val encoder: Encoder[Request] = Encoder.forProduct4("election", "numVacancies", "ineligibleCandidates", "doRounding")(r => (r.election, r.numVacancies, r.ineligibleCandidates, r.doRounding))

    implicit def decode(implicit decodeCandidate: Decoder[SenateCandidate]): Decoder[Request] =
      Decoder.forProduct4("election", "numVacancies", "ineligibleCandidates", "doRounding")(Request.apply)
  }

  object Outcome {

    implicit val encoder: Encoder[Outcome] = Encoder.forProduct4("elected", "exhaustedVotes", "roundingError", "candidateOutcomes")(o => (o.candidateOutcomes.electedCandidates, o.exhaustedVotes, o.roundingError, o.candidateOutcomes))

    implicit def decode(implicit decodeCandidate: Decoder[SenateCandidate]): Decoder[Outcome] =
      Decoder.forProduct4("elected", "exhaustedVotes", "roundingError", "candidateOutcomes")(Outcome.apply)

  }

  implicit val encoder: Encoder[CountSummary] = Encoder.forProduct2("request", "outcomePossibilities")(r => (r.request, r.outcomePossibilities))

  implicit def decode(implicit decodeCandidate: Decoder[SenateCandidate]): Decoder[CountSummary] =
    Decoder.forProduct2("request", "outcomePossibilities")(CountSummary.apply)

}
