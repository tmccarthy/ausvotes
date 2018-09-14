package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.aws.S3BucketName
import au.id.tmm.ausvotes.shared.io.actions.EnvVars
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad._

object Configuration {

  def recountDataBucketName[F[+_, +_] : EnvVars : Monad]: F[RecountLambdaError.RecountDataBucketUndefined.type, S3BucketName] =
    for {
      possibleRawBucketName <- EnvVars.envVar("RECOUNT_DATA_BUCKET")
      bucketName <- fromEither(possibleRawBucketName.toRight(RecountLambdaError.RecountDataBucketUndefined))
    } yield S3BucketName(bucketName)

}
