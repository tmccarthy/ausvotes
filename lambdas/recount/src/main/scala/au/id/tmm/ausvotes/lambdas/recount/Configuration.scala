package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.aws.S3BucketName
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.io.typeclasses.{AccessesEnvVars, Monad}

object Configuration {

  def recountDataBucketName[F[+_, +_] : AccessesEnvVars : Monad]: F[RecountLambdaError.RecountDataBucketUndefined.type, S3BucketName] =
    AccessesEnvVars.envVar("RECOUNT_DATA_BUCKET")
      .flatMap { possibleRawBucketName =>
        Monad.fromEither {
          possibleRawBucketName
            .map(S3BucketName)
            .toRight(RecountLambdaError.RecountDataBucketUndefined)
        }
      }

}
