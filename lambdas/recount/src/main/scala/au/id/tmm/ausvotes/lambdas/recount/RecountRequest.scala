package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.core.computations.numvacancies.NumVacanciesComputation
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError.RecountRequestError
import au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration.ApiGatewayLambdaRequest
import au.id.tmm.utilities.geo.australia.State

final case class RecountRequest(
                                 election: SenateElection,
                                 state: State,
                                 vacancies: Int,
                                 ineligibleCandidateAecIds: Set[String],
                               )

object RecountRequest {

  def fromRequest(request: ApiGatewayLambdaRequest): Either[RecountRequestError, RecountRequest] = for {
    election <- electionFrom(request)
    state <- stateFrom(request)
    numVacancies <- numVacanciesFrom(request, election, state)
    ineligibleCandidateAecIds = ineligibleCandidatesFrom(request)

    _ <- ensureElectionForState(election, state)
  } yield RecountRequest(election, state, numVacancies, ineligibleCandidateAecIds)

  private def electionFrom(request: ApiGatewayLambdaRequest): Either[RecountRequestError, SenateElection] =
    for {
      electionId <- request.pathParameters.get("election").toRight(RecountRequestError.MissingElection)
      election <- SenateElection.forId(electionId).toRight(RecountRequestError.InvalidElectionId(electionId))
    } yield election

  private def stateFrom(request: ApiGatewayLambdaRequest): Either[RecountRequestError, State] =
    for {
      stateAbbreviation <- request.pathParameters.get("state").toRight(RecountRequestError.MissingState)
      state <- State.fromAbbreviation(stateAbbreviation).toRight(RecountRequestError.InvalidStateId(stateAbbreviation))
    } yield state

  private def ensureElectionForState(
                                      election: SenateElection,
                                      state: State,
                                    ): Either[RecountRequestError.NoElectionForState, Unit] = {
    if (election.states contains state) {
      Right(Unit)
    } else {
      Left(RecountRequestError.NoElectionForState(election, state))
    }
  }

  private def numVacanciesFrom(
                                request: ApiGatewayLambdaRequest,
                                election: SenateElection,
                                state: State,
                              ): Either[RecountRequestError, Int] = {
    request.queryStringParameters.get("vacancies").map { rawNumVacancies =>
      try {
        val numVacancies = rawNumVacancies.toInt

        if (numVacancies < 1) {
          Left(RecountRequestError.InvalidNumVacancies(rawNumVacancies))
        } else {
          Right(numVacancies)
        }
      } catch {
        case _: NumberFormatException => Left(RecountRequestError.InvalidNumVacancies(rawNumVacancies))
      }
    }.getOrElse {
      NumVacanciesComputation.numVacanciesForStateAtElection(election, state)
        .left.map(_ => RecountRequestError.NoElectionForState(election, state))
    }
  }

  private def ineligibleCandidatesFrom(request: ApiGatewayLambdaRequest): Set[String] =
    request.queryStringParameters.get("ineligibleCandidates")
      .map(_.split(',').filter(_.nonEmpty).toSet)
      .getOrElse(Set.empty)

}
