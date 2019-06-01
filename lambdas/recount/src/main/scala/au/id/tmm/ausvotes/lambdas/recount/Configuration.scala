package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.bfect.BifunctorMonad.Ops
import au.id.tmm.bfect.effects.extra.EnvVars

object Configuration {

  def recountDataBucketName[F[+_, +_] : EnvVars]: F[RecountLambdaError.RecountDataBucketUndefined, S3BucketName] =
    EnvVars[F].envVarOrError("RECOUNT_DATA_BUCKET", RecountLambdaError.RecountDataBucketUndefined()).map(S3BucketName)

}
