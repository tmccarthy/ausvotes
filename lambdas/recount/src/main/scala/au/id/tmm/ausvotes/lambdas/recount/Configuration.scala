package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME._
import au.id.tmm.bfect.extraeffects.EnvVars

object Configuration {

  def recountDataBucketName[F[+_, +_] : EnvVars : BME]: F[RecountLambdaError.RecountDataBucketUndefined, S3BucketName] =
    EnvVars.envVarOrError("RECOUNT_DATA_BUCKET", RecountLambdaError.RecountDataBucketUndefined()).map(S3BucketName)

}
