package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.actions.EnvVars
import au.id.tmm.ausvotes.shared.io.typeclasses.{BifunctorMonadError => BME}
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops

object Configuration {

  def recountDataBucketName[F[+_, +_] : EnvVars : BME]: F[RecountLambdaError.RecountDataBucketUndefined, S3BucketName] =
    EnvVars.envVarOr("RECOUNT_DATA_BUCKET", RecountLambdaError.RecountDataBucketUndefined()).map(S3BucketName)

}
