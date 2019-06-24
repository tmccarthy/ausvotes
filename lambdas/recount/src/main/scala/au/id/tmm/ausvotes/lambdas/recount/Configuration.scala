package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.bfect.BFunctor
import au.id.tmm.bfect.BFunctor.Ops
import au.id.tmm.bfect.effects.extra.EnvVars

object Configuration {

  def recountDataBucketName[F[+_, +_] : EnvVars : BFunctor]: F[RecountLambdaError.RecountDataBucketUndefined, S3BucketName] =
    EnvVars[F].envVarOrError("RECOUNT_DATA_BUCKET", RecountLambdaError.RecountDataBucketUndefined()).map(S3BucketName)

}
