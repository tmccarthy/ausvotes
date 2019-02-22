package au.id.tmm.ausvotes.api.controllers

import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.errors.recount.RecountException
import au.id.tmm.ausvotes.api.model.recount.RecountApiRequest
import au.id.tmm.ausvotes.core.model.IneligibleCandidates
import au.id.tmm.ausvotes.shared.aws.actions.LambdaActions.InvokesLambda
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.{BifunctorMonadError => BME}
import au.id.tmm.ausvotes.shared.recountresources.exceptions.InvalidJsonException
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest}
import cats.syntax.show.toShow
import com.amazonaws.AmazonServiceException
import io.circe.Json
import io.circe.parser.parse
import io.circe.syntax.EncoderOps

class RecountController(config: Config) {

  // TODO should really return the decoded RecountResult
  def recount[F[+_, +_] : BME : ReadsS3 : InvokesLambda](apiRequest: RecountApiRequest): F[RecountException, Json] = {
    val recountRequest = buildFullRecountRequest(apiRequest)

    //noinspection ConvertibleToMethodValue
    for {
      cachedRecount <- readCachedRecount(recountRequest).leftMap(RecountException.CheckRecountComputedError)

      response <- cachedRecount.map(BME.pure(_))
        .getOrElse(requestRecount(recountRequest)).leftMap(RecountException.RequestRecountError)
    } yield response
  }

  private def buildFullRecountRequest(apiRequest: RecountApiRequest): RecountRequest = {
    val election = apiRequest.election

    def defaultNumVacancies = NumVacanciesComputation.numVacanciesFor(election)

    RecountRequest(
      election,
      apiRequest.numVacancies getOrElse defaultNumVacancies,
      apiRequest.ineligibleCandidates getOrElse IneligibleCandidates.ineligibleCandidatesFor(election),
      doRounding = true,
    )
  }

  private def readCachedRecount[F[+_, +_] : ReadsS3 : BME](recountRequest: RecountRequest): F[Exception, Option[Json]] = {
    val locationOfCachedRecount = RecountLocations.locationOfRecountFor(recountRequest)

    for {
      possibleRawRecountJson <- ReadsS3.readAsString(config.recountDataBucket, locationOfCachedRecount)
        .map(Some(_): Option[String])
        .catchLeft {
          case e: AmazonServiceException if e.getErrorCode == "NoSuchKey" => BME.pure(None)
        }
      possibleRecountJson <- possibleRawRecountJson.map(parse) match {
        case Some(Right(json)) => BME.pure(Some(json))
        case Some(Left(failure)) => BME.leftPure(InvalidJsonException(failure.show))
        case None => BME.pure(None)
      }
    } yield possibleRecountJson
  }

  private def requestRecount[F[+_, +_] : ReadsS3 : InvokesLambda : BME](recountRequest: RecountRequest): F[Exception, Json] = {
    val requestBody = recountRequest.asJson

    for {
      responseBody <- InvokesLambda.invokeFunction(config.recountFunction, Some(requestBody.noSpaces))
      responseJson <- BME.fromEither(parse(responseBody)).leftMap(f => InvalidJsonException(f.show))
    } yield responseJson
  }

}
