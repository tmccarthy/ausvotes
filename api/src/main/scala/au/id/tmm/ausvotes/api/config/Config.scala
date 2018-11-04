package au.id.tmm.ausvotes.api.config

import au.id.tmm.ausvotes.api.errors.ConfigException
import au.id.tmm.ausvotes.shared.aws.data.{LambdaFunctionName, S3BucketName}
import au.id.tmm.ausvotes.shared.io.actions.EnvVars
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.{MonadEitherOps, MonadOps}

final case class Config(
                         recountDataBucket: S3BucketName,
                         recountFunction: LambdaFunctionName,
                       )

object Config {

  def fromEnvironment[F[+_, +_] : EnvVars : Monad]: F[ConfigException, Config] =
    (EnvVars.envVars: F[ConfigException, Map[String, String]]).map { envVars =>
      for {
        recountDataBucket <- readS3BucketName(envVars, "RECOUNT_DATA_BUCKET")
          .getOrElse(Left(ConfigException.EnvVarMissing("RECOUNT_DATA_BUCKET")))

        recountFunction <- readLambdaFunctionName(envVars, "RECOUNT_LAMBDA_FUNCTION_NAME")
            .getOrElse(Right(LambdaFunctionName("recount")))
      } yield Config(recountDataBucket, recountFunction)
    }.absolve

  private def readS3BucketName(environment: Map[String, String], envVar: String): Option[Either[ConfigException, S3BucketName]] =
    readSimpleStringType(environment, envVar, S3BucketName)

  private def readLambdaFunctionName(environment: Map[String, String], envVar: String): Option[Either[ConfigException, LambdaFunctionName]] =
    readSimpleStringType(environment, envVar, LambdaFunctionName)

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
