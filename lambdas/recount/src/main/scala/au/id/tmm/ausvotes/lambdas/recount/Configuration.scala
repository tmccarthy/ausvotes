package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.actions.EnvVars
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps

object Configuration {

  def recountDataBucketName[F[+_, +_] : EnvVars : Monad]: F[RecountLambdaError.RecountDataBucketUndefined.type, S3BucketName] =
    EnvVars.envVarOr("RECOUNT_DATA_BUCKET", RecountLambdaError.RecountDataBucketUndefined).map(S3BucketName)

}
