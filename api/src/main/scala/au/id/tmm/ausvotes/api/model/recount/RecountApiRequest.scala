package au.id.tmm.ausvotes.api.model.recount

import au.id.tmm.ausvotes.api.model.recount.RecountApiRequest.ConstructionException.{InvalidElectionId, InvalidNumVacancies, InvalidRoundingFlag, InvalidStateId}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.utilities.geo.australia.State

final case class RecountApiRequest(
                                    election: SenateElection,
                                    state: State,
                                    numVacancies: Option[Int],
                                    ineligibleCandidates: Option[Set[AecCandidateId]],
                                    doRounding: Option[Boolean],
                                  )

object RecountApiRequest {

  def buildFrom(
                 rawElection: String,
                 rawState: String,
                 rawNumVacancies: Option[String],
                 rawIneligibleCandidates: Option[String],
                 rawDoRounding: Option[String],
               ): Either[ConstructionException, RecountApiRequest] =
    for {
      election <- SenateElection.forId(rawElection)
        .toRight(InvalidElectionId(rawElection))

      state <- State.fromAbbreviation(rawState)
        .toRight(InvalidStateId(rawState))

      _ <- ensureElectionForState(election, state)

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

      ineligibleCandidates = ineligibleCandidatesFrom(rawIneligibleCandidates)

      doRounding <- rawDoRounding match {
        case Some(rawDoRounding) => booleanFrom(rawDoRounding).map(Some(_)).toRight(InvalidRoundingFlag(rawDoRounding))
        case None => Right(None)
      }

    } yield RecountApiRequest(election, state, numVacancies, ineligibleCandidates, doRounding)

  private def ensureElectionForState(
                                      election: SenateElection,
                                      state: State,
                                    ): Either[ConstructionException.NoElectionForState, Unit] = {
    if (election.states contains state) {
      Right(Unit)
    } else {
      Left(ConstructionException.NoElectionForState(election, state))
    }
  }

  private def ineligibleCandidatesFrom(rawIneligibleCandidates: Option[String]): Option[Set[AecCandidateId]] =
    rawIneligibleCandidates
      .map(_.split(',').filter(_.nonEmpty).toSet.map(AecCandidateId(_)))

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
  }

}
