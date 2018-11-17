package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import au.id.tmm.ausvotes.core.model.codecs.CandidateCodec.encodeCandidate
import au.id.tmm.ausvotes.core.model.codecs.PartyCodec.encodeParty
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, CandidatePosition, Group}
import au.id.tmm.ausvotes.lambdas.recount.RecountLambda.rts
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness.{ErrorResponseTransformer, RequestDecodeError, RequestReadError}
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.WritesToS3
import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.Logging
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.actions.Log._
import au.id.tmm.ausvotes.shared.io.actions.{Log, Now}
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.io.typeclasses._
import au.id.tmm.ausvotes.shared.recountresources.entities.PreferenceTreeCache.GroupsCandidatesAndPreferences
import au.id.tmm.ausvotes.shared.recountresources.entities.{GroupsAndCandidatesCache, GroupsCache, PreferenceTreeCache}
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest, RecountResponse}
import au.id.tmm.countstv.model.preferences.PreferenceTree
import au.id.tmm.utilities.collection.Flyweight
import com.amazonaws.services.lambda.runtime.Context
import scalaz.zio.{IO, RTS}

final class RecountLambda extends LambdaHarness[RecountRequest, RecountResponse, RecountLambdaError](rts) {

  override def logic(lambdaRequest: RecountRequest, context: Context): IO[RecountLambdaError, RecountResponse] = {
    recountLogic(lambdaRequest, context)
  }

  private def recountLogic
  (
    recountRequest: RecountRequest,
    context: Context,
  ): IO[RecountLambdaError, RecountResponse] = {

    import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
    import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._

    for {
      recountDataBucketName <- Configuration.recountDataBucketName

      preferenceTreeCache = RecountLambda.preferenceTreeCache(recountDataBucketName)

      _ <- logInfo("RECEIVE_RECOUNT_REQUEST", "recount_request" -> recountRequest)

      election = recountRequest.election
      state = recountRequest.state

      recountResponse <- preferenceTreeCache.withGroupsCandidatesAndPreferencesWhilePopulatingCache(election, state)(
        action = {
          case GroupsCandidatesAndPreferences(groupsAndCandidates, preferenceTree) =>
            computeRecount(recountDataBucketName, recountRequest, groupsAndCandidates.groups, groupsAndCandidates.candidates, preferenceTree)
        },
        mapEntityFetchError = RecountLambdaError.EntityFetchError,
        mapCachePopulateError = RecountLambdaError.EntityCachePopulationError,
      )
    } yield recountResponse
  }

  private def computeRecount[F[+_, +_] : WritesToS3 : Log : Now : Monad]
  (
    recountDataBucketName: S3BucketName,
    recountRequest: RecountRequest,
    groups: Set[Group],
    candidates: Set[Candidate],
    preferenceTree: PreferenceTree.RootPreferenceTree[CandidatePosition],
  ): F[RecountLambdaError, RecountResponse] =
    for {
      ineligibleCandidates <- Monad.fromEither {
        CandidateActualisation.actualiseIneligibleCandidates(recountRequest.ineligibleCandidateAecIds, candidates)
      }

      //noinspection ConvertibleToMethodValue
      recountResult <- Logging.timedLog(
        "PERFORM_RECOUNT",
        "election" -> recountRequest.election,
        "state" -> recountRequest.state,
        "num_ineligible_candidates" -> ineligibleCandidates.size,
        "num_vacancies" -> recountRequest.vacancies,
      ) {
        PerformRecount.performRecount(
          recountRequest.election,
          recountRequest.state,
          candidates,
          preferenceTree,
          ineligibleCandidates,
          recountRequest.vacancies,
        )
      }

      recountResultKey = RecountLocations.locationOfRecountFor(recountRequest)

      _ <- WritesToS3.putJson(recountDataBucketName, recountResultKey)(
        content = recountResult.asJson.toString,
      )
        .timedLog("PUT_RECOUNT_RESULT",
          "recount_data_bucket_name" -> recountDataBucketName,
          "recount_result_key" -> recountResultKey,
        )
        .leftMap(RecountLambdaError.WriteRecountError)
    } yield RecountResponse.Success(recountResult)

  override protected def errorLogTransformer: RecountLambdaErrorLogTransformer.type = RecountLambdaErrorLogTransformer

  protected def errorResponseTransformer: ErrorResponseTransformer[RecountResponse, RecountLambdaError] = {
    case RecountLambdaError.RecountRequestError.InvalidCandidateIds(invalidCandidateAecIds) =>
      RecountResponse.Failure.InvalidCandidateIds(invalidCandidateAecIds)
    case _ => RecountResponse.Failure.InternalError
  }

  protected def transformHarnessError(harnessInputError: LambdaHarness.HarnessInputError): RecountResponse =
    harnessInputError match {
      case RequestReadError(_) => RecountResponse.Failure.InternalError
      case RequestDecodeError(message, request) => RecountResponse.Failure.RequestDecodeError(message, request)
    }

}

object RecountLambda {
  private lazy val rts: RTS = new RTS {}

  private val preferenceTreeCache: Flyweight[S3BucketName, PreferenceTreeCache] = Flyweight { s3BucketName =>
    rts.unsafeRun {
      for {
        groupsCache <- GroupsCache(s3BucketName)
        groupsAndCandidatesCache <- GroupsAndCandidatesCache(groupsCache)
        preferenceTreeCache <- PreferenceTreeCache(groupsAndCandidatesCache)
      } yield preferenceTreeCache
    }
  }
}
