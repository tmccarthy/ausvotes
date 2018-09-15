package au.id.tmm.ausvotes.shared.aws.testing

import au.id.tmm.ausvotes.shared.aws.{S3BucketName, S3ObjectKey}

object AwsTestDataUtils {

  trait S3Interaction[D] {
    def s3Content: Map[S3BucketName, Map[S3ObjectKey, List[String]]]

    protected def copyWithS3Content(s3Content: Map[S3BucketName, Map[S3ObjectKey, List[String]]]): D

    def writeString(bucketName: S3BucketName, objectKey: S3ObjectKey, content: String): D = {
      val mapWithDefaults = this.s3Content.withDefaultValue(Map.empty.withDefaultValue(Nil))

      copyWithS3Content(
        mapWithDefaults
          .updated(bucketName, this.s3Content(bucketName)
            .updated(objectKey, this.s3Content(bucketName)(objectKey) :+ content)
          )
      )
    }
  }

  trait SnsWrites[D] {
    def snsMessagesPerTopic: Map[String, List[String]]

    protected def copyWithSnsMessages(snsMessagesPerTopic: Map[String, List[String]]): D

    def writeMessage(topic: String, message: String): D = {
      val mapWithDefaults = this.snsMessagesPerTopic.withDefaultValue(Nil)

      copyWithSnsMessages(
        mapWithDefaults.updated(topic, mapWithDefaults(topic) :+ message)
      )
    }
  }

}
