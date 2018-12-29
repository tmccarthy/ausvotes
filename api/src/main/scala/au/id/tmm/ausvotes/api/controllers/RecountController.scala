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
import au.id.tmm.ausvotes.shared.io.typeclasses.{BifunctorMonadError => BME}
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.recountresources.exceptions.InvalidJsonException
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest}
import com.amazonaws.AmazonServiceException

class RecountController(config: Config) {

  // TODO should really return the decoded RecountResult
  def recount[F[+_, +_] : BME : ReadsS3 : InvokesLambda](apiRequest: RecountApiRequest): F[RecountException, Json] = {
    //noinspection ConvertibleToMethodValue
    for {
      recountRequest <- BME.fromEither(buildFullRecountRequest(apiRequest))

      cachedRecount <- readCachedRecount(recountRequest).leftMap(RecountException.CheckRecountComputedError)

      response <- cachedRecount.map(BME.pure(_))
        .getOrElse(requestRecount(recountRequest)).leftMap(RecountException.RequestRecountError)
    } yield response
  }

  private def buildFullRecountRequest(apiRequest: RecountApiRequest): Either[RecountException, RecountRequest] = {
    val election = apiRequest.election
    val state = apiRequest.state

    NumVacanciesComputation.numVacanciesFor(election, state).map { defaultNumVacancies =>
      RecountRequest(
        election,
        state,
        apiRequest.numVacancies getOrElse defaultNumVacancies,
        apiRequest.ineligibleCandidates getOrElse IneligibleCandidates.ineligibleCandidatesFor(election, state),
        doRounding = true,
      )
    }.left.map(_ => RecountException.BadRequestError(RecountApiRequest.ConstructionException.NoElectionForState(election, state)))
  }

  private def readCachedRecount[F[+_, +_] : ReadsS3 : BME](recountRequest: RecountRequest): F[Exception, Option[Json]] = {
    val locationOfCachedRecount = RecountLocations.locationOfRecountFor(recountRequest)

    for {
      possibleRawRecountJson <- ReadsS3.readAsString(config.recountDataBucket, locationOfCachedRecount)
        .map(Some(_): Option[String])
        .catchLeft {
          case e: AmazonServiceException if e.getErrorCode == "NoSuchKey" => BME.pure(None)
        }
      possibleRecountJson <- possibleRawRecountJson.map(Parse.parse) match {
        case Some(Right(json)) => BME.pure(Some(json))
        case Some(Left(errorMessage)) => BME.leftPure(InvalidJsonException(errorMessage))
        case None => BME.pure(None)
      }
    } yield possibleRecountJson
  }

  private def requestRecount[F[+_, +_] : ReadsS3 : InvokesLambda : BME](recountRequest: RecountRequest): F[Exception, Json] = {
    val requestBody = recountRequest.asJson

    for {
      responseBody <- InvokesLambda.invokeFunction(config.recountFunction, Some(requestBody.toString))
      responseJson <- BME.fromEither(Parse.parse(responseBody)).leftMap(InvalidJsonException)
    } yield responseJson
  }

}
