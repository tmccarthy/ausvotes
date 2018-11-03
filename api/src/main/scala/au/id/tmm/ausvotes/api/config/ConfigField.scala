package au.id.tmm.ausvotes.api.config

import au.id.tmm.ausvotes.shared.aws.data.{LambdaFunctionName, S3BucketName}

abstract class ConfigField[A](defaultValue: Option[A] = None)

object ConfigField {
  case object RecountDataBucket extends ConfigField[S3BucketName]
  case object RecountLambdaName extends ConfigField[LambdaFunctionName]
  case object AwsRegion extends ConfigField[String]
}
