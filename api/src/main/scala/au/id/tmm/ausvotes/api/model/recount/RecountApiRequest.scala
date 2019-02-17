package au.id.tmm.ausvotes.api.model.recount

import au.id.tmm.ausvotes.api.model.recount.RecountApiRequest.ConstructionException._
import au.id.tmm.ausvotes.model.CandidateDetails
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.utilities.geo.australia.State
import cats.implicits._

final case class RecountApiRequest(
                                    election: SenateElectionForState,
                                    numVacancies: Option[Int],
                                    ineligibleCandidates: Option[Set[CandidateDetails.Id]],
                                    doRounding: Option[Boolean],
                                  )

object RecountApiRequest {

  def buildFrom(
                 rawSenateElectionId: String,
                 rawState: String,
                 rawNumVacancies: Option[String],
                 rawIneligibleCandidates: Option[String],
                 rawDoRounding: Option[String],
               ): Either[ConstructionException, RecountApiRequest] =
    for {
      senateElection <- SenateElection.from(SenateElection.Id(rawSenateElectionId))
        .toRight(InvalidElectionId(rawSenateElectionId))

      state <- State.fromAbbreviation(rawState)
        .toRight(InvalidStateId(rawState))

      election <- SenateElectionForState(senateElection, state)
        .left.map(_ => ConstructionException.NoElectionForState(senateElection, state))

      numVacancies <- rawNumVacancies match {
        case Some(rawNumVacancies) =>
          try {
            val numVacancies = rawNumVacancies.toInt

            if (numVacancies < 1) {
              Left(InvalidNumVacancies(rawNumVacancies))
            } else {
              Right(Some(numVacancies))
            }
          } catch {
            case _: NumberFormatException => Left(InvalidNumVacancies(rawNumVacancies))
          }
        case None => Right(None)
      }

      ineligibleCandidates <- ineligibleCandidatesFrom(rawIneligibleCandidates)

      doRounding <- rawDoRounding match {
        case Some(rawDoRounding) => booleanFrom(rawDoRounding).map(Some(_)).toRight(InvalidRoundingFlag(rawDoRounding))
        case None => Right(None)
      }

    } yield RecountApiRequest(election, numVacancies, ineligibleCandidates, doRounding)

  private def ineligibleCandidatesFrom(rawIneligibleCandidates: Option[String]): Either[ConstructionException, Option[Set[CandidateDetails.Id]]] = {
    rawIneligibleCandidates.traverse { rawIneligibleCandidates =>
      val candidateIdsAsStrings = rawIneligibleCandidates.split(',').filter(_.nonEmpty)

      val (badIds, goodIds) = candidateIdsAsStrings.foldLeft((Set.empty[String], Set.empty[CandidateDetails.Id])) { case ((badIds, goodIds), nextId) =>
        try {
          val goodId = CandidateDetails.Id(nextId.toInt)
          (badIds, goodIds + goodId)
        } catch {
          case e: NumberFormatException => (badIds + nextId, goodIds)
        }
      }

      if (badIds.nonEmpty) {
        Left(InvalidCandidateIds(badIds))
      } else {
        Right(goodIds)
      }
    }
  }

  private def booleanFrom(string: String): Option[Boolean] =
    try Some(string.toBoolean) catch {
      case _: IllegalArgumentException => None
    }

  sealed trait ConstructionException extends ExceptionCaseClass

  object ConstructionException {
    final case class InvalidElectionId(badElectionId: String) extends ConstructionException
    final case class InvalidStateId(badStateId: String) extends ConstructionException
    final case class NoElectionForState(election: SenateElection, state: State) extends ConstructionException
    final case class InvalidNumVacancies(badNumVacancies: String) extends ConstructionException
    final case class InvalidRoundingFlag(badRoundingFlag: String) extends ConstructionException
    final case class InvalidCandidateIds(badCandidateIds: Set[String]) extends ConstructionException
  }

}
