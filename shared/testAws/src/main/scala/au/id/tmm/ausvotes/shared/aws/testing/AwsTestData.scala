package au.id.tmm.ausvotes.shared.aws.testing

import java.time.{Duration, Instant}

import au.id.tmm.ausvotes.shared.aws.data.LambdaFunctionName
import au.id.tmm.ausvotes.shared.aws.testing.datatraits.{LambdaInteraction, S3Interaction, SnsWrites}
import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.test
import au.id.tmm.ausvotes.shared.io.test.datatraits.{CurrentTime, EnvVars, Logging}

final case class AwsTestData(
                              envVars: Map[String, String] = Map.empty,

                              loggedMessages: Map[Log.Level, List[LoggedEvent]] = Map.empty.withDefaultValue(Nil),
                              initialTime: Instant = Instant.EPOCH,
                              stepEachInvocation: Duration = Duration.ofSeconds(1),

                              s3Content: S3Interaction.InMemoryS3 = S3Interaction.InMemoryS3.empty,

                              snsMessagesPerTopic: Map[String, List[String]] = Map.empty,

                              lambdaCallHandler: LambdaInteraction.Responder = LambdaInteraction.alwaysFailHandler,
                              lambdaInvocations: List[LambdaInteraction.Invocation] = Nil,
                            ) extends EnvVars
  with CurrentTime[AwsTestData]
  with Logging[AwsTestData]
  with S3Interaction[AwsTestData]
  with SnsWrites[AwsTestData]
  with LambdaInteraction[AwsTestData] {

  override protected def copyWithInitialTime(initialTime: Instant): AwsTestData = this.copy(initialTime = initialTime)

  override protected def copyWithLoggedMessages(loggedMessages: Map[Log.Level, List[LoggedEvent]]): AwsTestData = this.copy(loggedMessages = loggedMessages)

  override protected def copyWithS3Content(s3Content: S3Interaction.InMemoryS3): AwsTestData = this.copy(s3Content = s3Content)

  override protected def copyWithSnsMessages(snsMessagesPerTopic: Map[String, List[String]]): AwsTestData = this.copy(snsMessagesPerTopic = snsMessagesPerTopic)

  override protected def copyWithLambdaInvocations(invocations: List[(LambdaFunctionName, Option[String])]): AwsTestData = this.copy(lambdaInvocations = invocations)

}

object AwsTestData {
  type TestIO[+E, +A] = test.TestIO[E, A, AwsTestData]
}
