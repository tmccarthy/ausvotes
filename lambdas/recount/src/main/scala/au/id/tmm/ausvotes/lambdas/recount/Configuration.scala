package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.aws.S3BucketName
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.io.typeclasses.{AccessesEnvVars, Monad}

object Configuration {

  def recountDataBucketName[F[+_, +_] : AccessesEnvVars : Monad]: F[RecountLambdaError.ConfigurationError, S3BucketName] =
    AccessesEnvVars.envVar("RECOUNT_DATA_BUCKET")
      .flatMap[RecountLambdaError.ConfigurationError, S3BucketName] { possibleRawBucketName =>
      Monad.fromEither {
        possibleRawBucketName.map(S3BucketName)
          .toRight(RecountLambdaError.ConfigurationError.RecountDataBucketUndefined)
      }
    }

}
