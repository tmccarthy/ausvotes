package au.id.tmm.ausvotes.shared.recountresources

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.computations.numvacancies.NumVacanciesComputation
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.codecs.GeneralCodecs._
import au.id.tmm.utilities.geo.australia.State

final case class RecountRequest(
                                 election: SenateElection,
                                 state: State,
                                 vacancies: Int,
                                 ineligibleCandidateAecIds: Set[String],
                               )

object RecountRequest {

  implicit val encodeJson: EncodeJson[RecountRequest] =
    casecodec4(apply, unapply)("election", "state", "vacancies", "ineligibleCandidates")

  def build(
             rawElection: Option[String],
             rawState: Option[String],
             rawNumVacancies: Option[String],
             rawIneligibleCandidates: Option[String],
           ): Either[RecountRequest.Error, RecountRequest] =
    for {
      election <- electionFrom(rawElection)
      state <- stateFrom(rawState)

      _ <- ensureElectionForState(election, state)

      vacancies <- numVacanciesFrom(rawNumVacancies, election, state)
      ineligibleCandidates = ineligibleCandidatesFrom(rawIneligibleCandidates)
    } yield RecountRequest(election, state, vacancies, ineligibleCandidates)

  private def electionFrom(rawElection: Option[String]): Either[RecountRequest.Error, SenateElection] =
    for {
      electionId <- rawElection.toRight(RecountRequest.Error.MissingElection)
      election <- SenateElection.forId(electionId).toRight(RecountRequest.Error.InvalidElectionId(electionId))
    } yield election

  private def stateFrom(rawState: Option[String]): Either[RecountRequest.Error, State] =
    for {
      stateAbbreviation <- rawState.toRight(RecountRequest.Error.MissingState)
      state <- State.fromAbbreviation(stateAbbreviation).toRight(RecountRequest.Error.InvalidStateId(stateAbbreviation))
    } yield state

  private def ensureElectionForState(
                                      election: SenateElection,
                                      state: State,
                                    ): Either[RecountRequest.Error.NoElectionForState, Unit] = {
    if (election.states contains state) {
      Right(Unit)
    } else {
      Left(RecountRequest.Error.NoElectionForState(election, state))
    }
  }

  private def numVacanciesFrom(
                                rawNumVacancies: Option[String],
                                election: SenateElection,
                                state: State,
                              ): Either[RecountRequest.Error, Int] = {
    rawNumVacancies.map { rawNumVacancies =>
      try {
        val numVacancies = rawNumVacancies.toInt

        if (numVacancies < 1) {
          Left(RecountRequest.Error.InvalidNumVacancies(rawNumVacancies))
        } else {
          Right(numVacancies)
        }
      } catch {
        case _: NumberFormatException => Left(RecountRequest.Error.InvalidNumVacancies(rawNumVacancies))
      }
    }.getOrElse {
      NumVacanciesComputation.numVacanciesForStateAtElection(election, state)
        .left.map(_ => RecountRequest.Error.NoElectionForState(election, state))
    }
  }

  private def ineligibleCandidatesFrom(rawIneligibleCandidates: Option[String]): Set[String] =
    rawIneligibleCandidates
      .map(_.split(',').filter(_.nonEmpty).toSet)
      .getOrElse(Set.empty)

  sealed trait Error

  object Error {
    case object MissingElection extends Error
    final case class InvalidElectionId(badElectionId: String) extends Error

    case object MissingState extends Error
    final case class InvalidStateId(badStateId: String) extends Error
    final case class NoElectionForState(election: SenateElection, state: State) extends Error

    final case class InvalidNumVacancies(badNumVacancies: String) extends Error

    def humanReadableMessageFor(error: Error): String = error match {
      case MissingElection =>
        "Election was not specified"

      case InvalidElectionId(badElectionId) =>
        s"""Unrecognised election id "$badElectionId""""

      case MissingState =>
        "State was not specified"

      case InvalidStateId(badStateId) =>
        s"""Unrecognised state id "$badStateId""""

      case NoElectionForState(election, state) =>
        s"""The election "${election.id}" did not have an election for state "${state.abbreviation}""""

      case InvalidNumVacancies(badNumVacancies) =>
        s"""Invalid number of vacancies "$badNumVacancies""""

    }
  }

}