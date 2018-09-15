package au.id.tmm.ausvotes.shared.aws.testing

import java.io.{InputStream, OutputStream}

import au.id.tmm.ausvotes.shared.aws.actions._
import au.id.tmm.ausvotes.shared.aws.{S3BucketName, S3ObjectKey}
import au.id.tmm.ausvotes.shared.io.test.TestIO
import com.amazonaws.SdkClientException

object AwsTestIoInstances {

  implicit def testIoWritesToS3[D <: AwsTestDataUtils.S3Interaction[D]]: S3Actions.WritesToS3[TestIO[+?, +?, D]] = new S3Actions.WritesToS3[TestIO[+?, +?, D]] {
    override def putString(bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String): TestIO[Exception, Unit, D] = TestIO { data =>
      data.writeString(bucketName, objectKey, content) -> Right(Unit)
    }

    //noinspection NotImplementedCode
    override def putFromOutputStream(bucketName: S3BucketName, objectKey: S3ObjectKey)(writeToOutputStream: OutputStream => TestIO[Exception, Unit, D]): TestIO[Exception, Unit, D] = ???
  }

  implicit def testIoReadsS3[D <: AwsTestDataUtils.S3Interaction[D]]: S3Actions.ReadsS3[TestIO[+?, +?, D]] = new S3Actions.ReadsS3[TestIO[+?, +?, D]] {
    override def readAsString(bucketName: S3BucketName, objectKey: S3ObjectKey): TestIO[Exception, String, D] = TestIO { data =>
      val content = for {
        contentForBucket <- data.s3Content.get(bucketName).toRight(new SdkClientException("No such bucket"))
        versionsOfObject <- contentForBucket.get(objectKey).toRight[Exception](new SdkClientException("Object not found"))
        objectContent <- versionsOfObject.headOption.toRight[Exception](new SdkClientException("Object not found"))
      } yield objectContent

      data -> content
    }

    //noinspection NotImplementedCode
    override def useInputStream[A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: InputStream => TestIO[Exception, A, D]): TestIO[Exception, A, D] = ???

    override def checkObjectExists(bucketName: S3BucketName, objectKey: S3ObjectKey): TestIO[Exception, Boolean, D] = TestIO { data =>
      val objectPresent = for {
        contentForBucket <- data.s3Content.get(bucketName).toRight(new SdkClientException("No such bucket"))
        versionsOfObject = contentForBucket.get(objectKey)
        objectPresent = versionsOfObject.nonEmpty
      } yield objectPresent

      data -> objectPresent
    }
  }

  implicit def testIoWritesToSns[D <: AwsTestDataUtils.SnsWrites[D]]: SnsActions.PutsSnsMessages[TestIO[+?, +?, D]] = new SnsActions.PutsSnsMessages[TestIO[+?, +?, D]] {
    override def putMessage(topicArn: String, messageBody: String): TestIO[Exception, Unit, D] = TestIO { data =>
      data.writeMessage(topicArn, messageBody) -> Right(Unit)
    }
  }

}
