package au.id.tmm.ausvotes.api.controllers

import argonaut.Argonaut._
import argonaut.{Json, Parse}
import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.errors.InvalidJsonException
import au.id.tmm.ausvotes.api.errors.recount.RecountException
import au.id.tmm.ausvotes.api.errors.recount.RecountException.BadRequestError
import au.id.tmm.ausvotes.shared.aws.actions.LambdaActions.InvokesLambda
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest}
import com.amazonaws.AmazonServiceException

class RecountController(config: Config) {

  def recount[F[+_, +_] : Monad : ReadsS3 : InvokesLambda](
                                                            rawElection: String,
                                                            rawState: String,
                                                            queryParams: Map[String, List[String]],
                                                          ): F[RecountException, Json] = {
    //noinspection ConvertibleToMethodValue
    for {
      recountRequest <- Monad.fromEither(buildRecountRequest(rawElection, rawState, queryParams)): F[RecountException, RecountRequest]

      cachedRecount <- readCachedRecount(recountRequest).leftMap(RecountException.CheckRecountComputedError)

      response <- cachedRecount.map(Monad.pure(_))
        .getOrElse(requestRecount(recountRequest)).leftMap(RecountException.RequestRecountError)
    } yield response
  }

  private def buildRecountRequest(
                                   rawElection: String,
                                   rawState: String,
                                   queryParams: Map[String, List[String]],
                                 ): Either[BadRequestError, RecountRequest] =
    RecountRequest.build(
      Some(rawElection),
      Some(rawState),
      rawNumVacancies = queryParams.get("vacancies").flatMap(_.headOption),
      rawIneligibleCandidates = queryParams.get("ineligibleCandidates").flatMap(_.headOption),
    ).left.map(RecountException.BadRequestError)

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
