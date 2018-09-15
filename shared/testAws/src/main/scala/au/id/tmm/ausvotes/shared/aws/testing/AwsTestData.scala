package au.id.tmm.ausvotes.shared.aws.testing

import java.time.{Duration, Instant}

import au.id.tmm.ausvotes.shared.aws.testing.AwsTestDataUtils.{S3Interaction, SnsWrites}
import au.id.tmm.ausvotes.shared.aws.{S3BucketName, S3ObjectKey}
import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.test
import au.id.tmm.ausvotes.shared.io.test.TestDataUtils._

final case class AwsTestData(
                              envVars: Map[String, String] = Map.empty,

                              loggedMessages: Map[Log.Level, List[LoggedEvent]] = Map.empty.withDefaultValue(Nil),
                              initialTime: Instant = Instant.EPOCH,
                              stepEachInvocation: Duration = Duration.ofSeconds(1),

                              s3Content: Map[S3BucketName, Map[S3ObjectKey, List[String]]] = Map.empty,

                              snsMessagesPerTopic: Map[String, List[String]] = Map.empty,
                            ) extends EnvVars[AwsTestData]
  with CurrentTime[AwsTestData]
  with Logging[AwsTestData]
  with S3Interaction[AwsTestData]
  with SnsWrites[AwsTestData] {

  override protected def copyWithInitialTime(initialTime: Instant): AwsTestData = this.copy(initialTime = initialTime)

  override protected def copyWithLoggedMessages(loggedMessages: Map[Log.Level, List[LoggedEvent]]): AwsTestData = this.copy(loggedMessages = loggedMessages)

  override protected def copyWithS3Content(s3Content: Map[S3BucketName, Map[S3ObjectKey, List[String]]]): AwsTestData = this.copy(s3Content = s3Content)

  override protected def copyWithSnsMessages(snsMessagesPerTopic: Map[String, List[String]]): AwsTestData = this.copy(snsMessagesPerTopic = snsMessagesPerTopic)

}

object AwsTestData {
  type TestIO[+E, +A] = test.TestIO[E, A, AwsTestData]
}