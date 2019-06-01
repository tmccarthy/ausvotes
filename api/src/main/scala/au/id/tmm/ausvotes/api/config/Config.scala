package au.id.tmm.ausvotes.api.config

import au.id.tmm.ausvotes.api.errors.ConfigException
import au.id.tmm.ausvotes.shared.aws.data.{LambdaFunctionName, S3BucketName}
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME.{AbsolveOps, Ops}
import au.id.tmm.bfect.effects.extra.EnvVars

final case class Config(
                         recountDataBucket: S3BucketName,
                         recountFunction: LambdaFunctionName,
                         basePath: List[String],
                       )

object Config {

  def fromEnvironment[F[+_, +_] : EnvVars : BME]: F[ConfigException, Config] =
    (EnvVars[F].envVars: F[ConfigException, Map[String, String]]).map { envVars =>
      for {
        recountDataBucket <- readS3BucketName(envVars, "RECOUNT_DATA_BUCKET")
          .getOrElse(Right(S3BucketName("recount-data.buckets.ausvotes.info")))

        recountFunction <- readLambdaFunctionName(envVars, "RECOUNT_LAMBDA_FUNCTION_NAME")
          .getOrElse(Right(LambdaFunctionName("recount")))

        basePath = readPath(envVars, "BASE_PATH")
          .getOrElse(List.empty)

      } yield Config(recountDataBucket, recountFunction, basePath)
    }.absolve

  private def readS3BucketName(environment: Map[String, String], envVar: String): Option[Either[ConfigException, S3BucketName]] =
    readSimpleStringType(environment, envVar, S3BucketName)

  private def readLambdaFunctionName(environment: Map[String, String], envVar: String): Option[Either[ConfigException, LambdaFunctionName]] =
    readSimpleStringType(environment, envVar, LambdaFunctionName)

  private def readPath(environment: Map[String, String], envVar: String): Option[List[String]] =
    environment.get(envVar).map(_.split('/').toList.filter(_.nonEmpty))

  private def readSimpleStringType[A](
                                       environment: Map[String, String],
                                       envVar: String,
                                       constructor: String => A,
                                     ): Option[Either[ConfigException.InvalidConfig, A]] =
    environment.get(envVar).map {
      case "" => Left(ConfigException.InvalidConfig(envVar, ""))
      case asString => Right(constructor(asString))
    }

}
