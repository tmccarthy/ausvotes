package au.id.tmm.ausvotes.api.controllers

import argonaut.Argonaut._
import argonaut.{Json, Parse}
import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.errors.recount.RecountException
import au.id.tmm.ausvotes.api.model.recount.RecountApiRequest
import au.id.tmm.ausvotes.core.computations.numvacancies.NumVacanciesComputation
import au.id.tmm.ausvotes.core.model.IneligibleCandidates
import au.id.tmm.ausvotes.shared.aws.actions.LambdaActions.InvokesLambda
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.recountresources.exceptions.InvalidJsonException
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest}
import com.amazonaws.AmazonServiceException

class RecountController(config: Config) {

  // TODO should really return the decoded RecountResult
  def recount[F[+_, +_] : Monad : ReadsS3 : InvokesLambda](apiRequest: RecountApiRequest): F[RecountException, Json] = {
    //noinspection ConvertibleToMethodValue
    for {
      recountRequest <- Monad.fromEither(buildFullRecountRequest(apiRequest))

      cachedRecount <- readCachedRecount(recountRequest).leftMap(RecountException.CheckRecountComputedError)

      response <- cachedRecount.map(Monad.pure(_))
        .getOrElse(requestRecount(recountRequest)).leftMap(RecountException.RequestRecountError)
    } yield response
  }

  private def buildFullRecountRequest(apiRequest: RecountApiRequest): Either[RecountException, RecountRequest] = {
    val election = apiRequest.election
    val state = apiRequest.state

    NumVacanciesComputation.numVacanciesForStateAtElection(election, state).map { defaultNumVacancies =>
      RecountRequest(
        election,
        state,
        apiRequest.numVacancies getOrElse defaultNumVacancies,
        apiRequest.ineligibleCandidates getOrElse IneligibleCandidates.ineligibleCandidatesFor(election, state),
      )
    }.left.map(_ => RecountException.BadRequestError(RecountApiRequest.ConstructionException.NoElectionForState(election, state)))
  }

  private def readCachedRecount[F[+_, +_] : ReadsS3 : Monad](recountRequest: RecountRequest): F[Exception, Option[Json]] = {
    val locationOfCachedRecount = RecountLocations.locationOfRecountFor(recountRequest)

    for {
      possibleRawRecountJson <- ReadsS3.readAsString(config.recountDataBucket, locationOfCachedRecount)
        .map(Some(_))
        .catchLeft {
          case e: AmazonServiceException if e.getErrorCode == "NoSuchKey" => Monad.pure(None)
        }
      possibleRecountJson <- possibleRawRecountJson.map(Parse.parse) match {
        case Some(Right(json)) => Monad.pure(Some(json))
        case Some(Left(errorMessage)) => Monad.leftPure(InvalidJsonException(errorMessage))
        case None => Monad.pure(None)
      }
    } yield possibleRecountJson
  }

  private def requestRecount[F[+_, +_] : ReadsS3 : InvokesLambda : Monad](recountRequest: RecountRequest): F[Exception, Json] = {
    val requestBody = recountRequest.asJson

    for {
      responseBody <- InvokesLambda.invokeFunction(config.recountFunction, Some(requestBody.toString))
      responseJson <- Monad.fromEither(Parse.parse(responseBody)).leftMap(InvalidJsonException)
    } yield responseJson
  }

}
