package au.id.tmm.ausvotes.api.config

import au.id.tmm.ausvotes.api.config.ConfigSpec.ConfigFieldSpec
import au.id.tmm.ausvotes.api.errors.ConfigException
import au.id.tmm.ausvotes.shared.aws.data.{LambdaFunctionName, S3BucketName}
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO._
import au.id.tmm.ausvotes.shared.io.test.testdata.EnvVarTestData
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ConfigSpec extends ImprovedFlatSpec {

  private val configFieldSpecs = List[ConfigFieldSpec[Any]](
    ConfigFieldSpec(
      "recountDataBucket",
      _.recountDataBucket,
      "RECOUNT_DATA_BUCKET",
      None,
      "bucketName" -> Right(S3BucketName("bucketName")),
      "" -> Left(ConfigException.InvalidConfig("RECOUNT_DATA_BUCKET", "")),
    ),
    ConfigFieldSpec(
      "recountFunction",
      _.recountFunction,
      "RECOUNT_LAMBDA_FUNCTION_NAME",
      Some(LambdaFunctionName("recount")),
      "recount" -> Right(LambdaFunctionName("recount")),
      "" -> Left(ConfigException.InvalidConfig("RECOUNT_LAMBDA_FUNCTION_NAME", ""))
    ),
  )

  private def retrieve[A](configField: Config => A, environment: Map[String, String]): Either[ConfigException, A] =
    Config.fromEnvironment[BasicTestIO].map(configField).run(BasicTestData(envVarTestData = EnvVarTestData(envVars = environment))).result

  private val greenPathEnvironment: Map[String, String] = configFieldSpecs.map { spec =>
    val aValidValueForThisField = spec.parsingTests.collect {
      case (rawValue, Right(_)) => rawValue
    }.head

    spec.environmentVar -> aValidValueForThisField
  }.toMap

  private def addTestsFor[A](spec: ConfigFieldSpec[A]): Unit = {
    behaviour of s"the ${spec.name} field"

    spec.defaultValue match {
      case Some(defaultValue) => it should s"have a default value of ${spec.defaultValue}" in {
        val environment = greenPathEnvironment - spec.environmentVar
        assert(retrieve(spec.field, environment) === Right(defaultValue))
      }
      case None => it should "fail if no env var is provided" in {
        val environment = greenPathEnvironment - spec.environmentVar
        assert(retrieve(spec.field, environment) === Left(ConfigException.EnvVarMissing(spec.environmentVar)))
      }
    }

    spec.parsingTests.foreach { case (environmentValue, expectedResult) =>
      val environment = greenPathEnvironment.updated(spec.environmentVar, environmentValue)

      val testDescription = expectedResult match {
        case Right(successfullyParsedValue) => s"""parse ${spec.environmentVar}="$environmentValue" as $successfullyParsedValue"""
        case Left(exception) => s"""fail to parse ${spec.environmentVar}="$environmentValue" with $exception"""
      }

      it should testDescription in {
        assert(retrieve(spec.field, environment) === expectedResult)
      }
    }
  }

  configFieldSpecs.foreach(addTestsFor)

}

object ConfigSpec {
  final case class ConfigFieldSpec[A](
                                       name: String,
                                       field: Config => A,
                                       environmentVar: String,
                                       defaultValue: Option[A],
                                       parsingTests: (String, Either[ConfigException, A])*,
                                     )
}
