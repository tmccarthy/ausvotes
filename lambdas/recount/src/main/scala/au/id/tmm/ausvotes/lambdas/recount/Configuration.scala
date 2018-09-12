package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.aws.S3BucketName
import au.id.tmm.ausvotes.shared.io.actions.EnvVars
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps

object Configuration {

  def recountDataBucketName[F[+_, +_] : Monad](implicit envVars: EnvVars[F]): F[RecountLambdaError.RecountDataBucketUndefined.type, S3BucketName] =
    envVars.envVar("RECOUNT_DATA_BUCKET")
      .flatMap { possibleRawBucketName =>
        Monad.fromEither {
          possibleRawBucketName
            .map(S3BucketName)
            .toRight(RecountLambdaError.RecountDataBucketUndefined)
        }
      }

}
