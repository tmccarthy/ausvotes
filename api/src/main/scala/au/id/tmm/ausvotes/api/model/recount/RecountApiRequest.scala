package au.id.tmm.ausvotes.api.model.recount

import au.id.tmm.ausvotes.api.model.recount.RecountApiRequest.ConstructionException.{InvalidElectionId, InvalidNumVacancies, InvalidStateId}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.utilities.geo.australia.State

final case class RecountApiRequest(
                                    election: SenateElection,
                                    state: State,
                                    numVacancies: Option[Int],
                                    ineligibleCandidates: Option[Set[AecCandidateId]],
                                  )

object RecountApiRequest {

  def buildFrom(
                 rawElection: String,
                 rawState: String,
                 rawNumVacancies: Option[String],
                 rawIneligibleCandidates: Option[String],
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

    } yield RecountApiRequest(election, state, numVacancies, ineligibleCandidates)

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

  sealed trait ConstructionException extends ExceptionCaseClass

  object ConstructionException {
    final case class InvalidElectionId(badElectionId: String) extends ConstructionException
    final case class InvalidStateId(badStateId: String) extends ConstructionException
    final case class NoElectionForState(election: SenateElection, state: State) extends ConstructionException
    final case class InvalidNumVacancies(badNumVacancies: String) extends ConstructionException

    def humanReadableMessageFor(exception: ConstructionException): String = exception match {
      case InvalidElectionId(badElectionId) =>
        s"""Unrecognised election id "$badElectionId""""

      case InvalidStateId(badStateId) =>
        s"""Unrecognised state id "$badStateId""""

      case NoElectionForState(election, state) =>
        s"""The election "${election.id}" did not have an election for state "${state.abbreviation}""""

      case InvalidNumVacancies(badNumVacancies) =>
        s"""Invalid number of vacancies "$badNumVacancies""""

    }
  }

}
