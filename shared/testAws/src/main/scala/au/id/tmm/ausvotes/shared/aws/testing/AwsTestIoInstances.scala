package au.id.tmm.ausvotes.shared.aws.testing

import java.io.{InputStream, OutputStream}

import au.id.tmm.ausvotes.shared.aws.actions._
import au.id.tmm.ausvotes.shared.aws.data.{ContentType, S3BucketName, S3ObjectKey}
import au.id.tmm.ausvotes.shared.aws.testing.datatraits.{S3Interaction, SnsWrites}
import au.id.tmm.ausvotes.shared.io.test.TestIO
import com.amazonaws.SdkClientException

object AwsTestIoInstances {

  implicit def testIoWritesToS3[D <: S3Interaction[D]]: S3Actions.WritesToS3[TestIO[+?, +?, D]] = new S3Actions.WritesToS3[TestIO[+?, +?, D]] {
    override def putString(bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String, contentType: ContentType): TestIO[Exception, Unit, D] = TestIO { data =>
      data.writeString(bucketName, objectKey, content, contentType) -> Right(Unit)
    }

    //noinspection NotImplementedCode
    override def putFromOutputStream(bucketName: S3BucketName, objectKey: S3ObjectKey)(writeToOutputStream: OutputStream => TestIO[Exception, Unit, D]): TestIO[Exception, Unit, D] = ???
  }

  implicit def testIoReadsS3[D <: S3Interaction[D]]: S3Actions.ReadsS3[TestIO[+?, +?, D]] = new S3Actions.ReadsS3[TestIO[+?, +?, D]] {
    override def readAsString(bucketName: S3BucketName, objectKey: S3ObjectKey): TestIO[Exception, String, D] = TestIO { data =>
      val content = for {
        contentForBucket <- data.s3Content(bucketName).toRight(new SdkClientException("No such bucket"))
        s3Object <- contentForBucket(objectKey).toRight[Exception](new SdkClientException("Object not found"))
      } yield s3Object.content

      data -> content
    }

    //noinspection NotImplementedCode
    override def useInputStream[A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: InputStream => TestIO[Exception, A, D]): TestIO[Exception, A, D] = ???

    override def checkObjectExists(bucketName: S3BucketName, objectKey: S3ObjectKey): TestIO[Exception, Boolean, D] = TestIO { data =>
      val hasObject = for {
        contentForBucket <- data.s3Content(bucketName).toRight(new SdkClientException("No such bucket"))
        hasObject = contentForBucket.hasObject(objectKey)
      } yield hasObject

      data -> hasObject
    }
  }

  implicit def testIoWritesToSns[D <: SnsWrites[D]]: SnsActions.PutsSnsMessages[TestIO[+?, +?, D]] = new SnsActions.PutsSnsMessages[TestIO[+?, +?, D]] {
    override def putMessage(topicArn: String, messageBody: String): TestIO[Exception, Unit, D] = TestIO { data =>
      data.writeMessage(topicArn, messageBody) -> Right(Unit)
    }
  }

}
