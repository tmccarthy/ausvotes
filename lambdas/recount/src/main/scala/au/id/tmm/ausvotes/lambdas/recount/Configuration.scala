package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.aws.S3BucketName
import scalaz.zio.IO

object Configuration {

  def recountDataBucketName: IO[RecountLambdaError.ConfigurationError, S3BucketName] =
    IO.sync {
      sys.env.get("RECOUNT_DATA_BUCKET")
    }.flatMap { possibleRawBucketName =>
      IO.fromEither {
        possibleRawBucketName.map(S3BucketName)
          .toRight(RecountLambdaError.ConfigurationError.RecountDataBucketUndefined)
      }
    }

}
