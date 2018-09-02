package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import argonaut.DecodeJson
import au.id.tmm.ausvotes.core.model.codecs.{CandidateCodec, GroupCodec, PartyCodec}
import au.id.tmm.ausvotes.lambdas.recount.RecountLambda.SnsMessage
import au.id.tmm.ausvotes.lambdas.utils.snsintegration.{SnsLambdaHarness, SnsLambdaRequest}
import au.id.tmm.ausvotes.shared.aws.S3Ops
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest}
import com.amazonaws.services.lambda.runtime.Context
import scalaz.zio.IO

final class RecountLambda extends SnsLambdaHarness[SnsMessage, RecountLambdaError] {

  override def logic(lambdaRequest: SnsLambdaRequest[SnsMessage], context: Context): IO[RecountLambdaError, Unit] = {
    implicit val partyCodec: PartyCodec = PartyCodec()
    implicit val groupCodec: GroupCodec = GroupCodec()

    for {
      recountDataBucketName <- Configuration.recountDataBucketName

      recountRequest <- IO.fromEither {
        val snsMessage = lambdaRequest.snsBody.message

        RecountRequest.build(
          rawElection = snsMessage.election,
          rawState = snsMessage.state,
          rawNumVacancies = snsMessage.vacancies,
          rawIneligibleCandidates = snsMessage.ineligibleCandidates,
        ).left.map(RecountLambdaError.RecountRequestError.RecountRequestParseError)
      }

      election = recountRequest.election
      state = recountRequest.state

      groups <- EntityFetching.fetchGroups(recountDataBucketName, election, state)

      candidateCodec = CandidateCodec(groups)
      candidates <- EntityFetching.fetchCandidates(recountDataBucketName, election, state)(candidateCodec)

      ineligibleCandidates <- IO.fromEither {
        CandidateActualisation.actualiseIneligibleCandidates(recountRequest.ineligibleCandidateAecIds, candidates)
      }

      preferenceTree <- EntityFetching.fetchPreferenceTree(recountDataBucketName, election, state, candidates)

      recountResult <- IO.fromEither {
        PerformRecount.performRecount(
          election,
          state,
          candidates,
          preferenceTree,
          ineligibleCandidates,
          recountRequest.vacancies,
        )
      }

      _ <- S3Ops.putString(
        bucketName = recountDataBucketName,
        objectKey = RecountLocations.locationOfRecountFor(recountRequest),
        content = recountResult.asJson(PerformRecount.Result.encodeRecountResult(candidateCodec)).toString,
      ).leftMap(RecountLambdaError.WriteRecountError)
    } yield Unit
  }

  override protected def errorLogTransformer: RecountLambdaErrorLogTransformer.type = RecountLambdaErrorLogTransformer

}

object RecountLambda {

  final case class SnsMessage(
                               election: Option[String],
                               state: Option[String],
                               vacancies: Option[String],
                               ineligibleCandidates: Option[String],
                             )

  object SnsMessage {
    implicit val decode: DecodeJson[SnsMessage] = c => for {
      election <- c.downField("election").as[Option[String]]
      state <- c.downField("state").as[Option[String]]
      vacancies <- c.downField("vacancies").as[Option[String]]
      ineligibleCandidates <- c.downField("ineligibleCandidates").as[Option[String]]
    } yield SnsMessage(election, state, vacancies, ineligibleCandidates)
  }

}