package au.id.tmm.ausvotes.shared.aws.testing.testdata

import java.io.{InputStream, OutputStream}

import au.id.tmm.ausvotes.shared.aws.actions.S3Actions
import au.id.tmm.ausvotes.shared.aws.data.{ContentType, S3BucketName, S3ObjectKey}
import au.id.tmm.ausvotes.shared.aws.testing.testdata.S3TestData.InMemoryS3
import au.id.tmm.ausvotes.shared.aws.testing.testdata.S3TestData.InMemoryS3.{Bucket, S3Object}
import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO.Output
import com.amazonaws.{AmazonServiceException, SdkClientException}

final case class S3TestData(
                             s3Content: InMemoryS3,
                           ) {
  def writeString(bucketName: S3BucketName, objectKey: S3ObjectKey, content: String, contentType: ContentType): S3TestData =
    copy(
      s3Content = s3Content.addObject(bucketName, InMemoryS3.S3Object(objectKey, content, contentType))
    )
}

object S3TestData {

  val empty = S3TestData(InMemoryS3.empty)

  def testIoReadS3Instance[D](
                               s3TestDataField: D => S3TestData,
                             ): S3Actions.ReadsS3[TestIO[D, +?, +?]] = new S3Actions.ReadsS3[TestIO[D, +?, +?]] {
    override def readAsString(bucketName: S3BucketName, objectKey: S3ObjectKey): TestIO[D, Exception, String] =
      TestIO { data =>
        val s3TestData = s3TestDataField(data)

        val content = for {
          contentForBucket <- s3TestData.s3Content(bucketName).toRight {
            val exception = new AmazonServiceException("No such bucket")
            exception.setStatusCode(404)
            exception.setErrorCode("NoSuchBucket")
            exception
          }
          s3Object <- contentForBucket(objectKey).toRight[Exception] {
            val exception = new AmazonServiceException("Object not found")
            exception.setStatusCode(404)
            exception.setErrorCode("NoSuchKey")
            exception
          }
        } yield s3Object.content

        Output(data, content)
      }

    //noinspection NotImplementedCode
    override def useInputStream[A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: InputStream => TestIO[D, Exception, A]): TestIO[D, Exception, A] = ???

    override def checkObjectExists(bucketName: S3BucketName, objectKey: S3ObjectKey): TestIO[D, Exception, Boolean] = TestIO { data =>
      val s3TestData = s3TestDataField(data)

      val hasObject = for {
        contentForBucket <- s3TestData.s3Content(bucketName).toRight(new SdkClientException("No such bucket"))
        hasObject = contentForBucket.hasObject(objectKey)
      } yield hasObject

      Output(data, hasObject)
    }
  }

  def testIoWriteS3Instance[D](
                                s3TestDataField: D => S3TestData,
                                setS3TestData: (D, S3TestData) => D,
                              ): S3Actions.WritesToS3[TestIO[D, +?, +?]] = new S3Actions.WritesToS3[TestIO[D, +?, +?]] {
    override def putString(bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String, contentType: ContentType): TestIO[D, Exception, Unit] =
      TestIO { oldTestData =>

        val oldS3TestData = s3TestDataField(oldTestData)

        val newS3TestData = oldS3TestData.writeString(bucketName, objectKey, content, contentType)

        val newTestData = setS3TestData(oldTestData, newS3TestData)

        Output(newTestData, Right(Unit))
      }

    //noinspection NotImplementedCode
    override def putFromOutputStream(bucketName: S3BucketName, objectKey: S3ObjectKey)(writeToOutputStream: OutputStream => TestIO[D, Exception, Unit]): TestIO[D, Exception, Unit] = ???
  }


  final case class InMemoryS3(buckets: Set[InMemoryS3.Bucket]) {
    def apply(name: S3BucketName): Option[Bucket] = buckets.find(_.name == name)
    def hasBucket(name: S3BucketName): Boolean = buckets.exists(_.name == name)
    def addBucket(name: S3BucketName): InMemoryS3 = this.copy(buckets = buckets + Bucket(name, Set.empty))
    def addObject(bucketName: S3BucketName, s3Object: S3Object): InMemoryS3 = {
      val oldBucket = this(bucketName).get
      val newBucket = oldBucket.addObject(s3Object)

      this.copy(buckets - oldBucket + newBucket)
    }
  }

  object InMemoryS3 {
    val empty = InMemoryS3(Set.empty[InMemoryS3.Bucket])

    def apply(contents: ((S3BucketName, S3ObjectKey), (String, ContentType))*): InMemoryS3 = {
      val objectsPerBucket: Map[S3BucketName, Set[InMemoryS3.S3Object]] = contents.groupBy {
        case ((bucketName, _), _) => bucketName
      }.map {
        case (bucketName, recordsForBucket) =>
          bucketName -> recordsForBucket.map {
            case ((_, objectKey), (content, contentType)) => InMemoryS3.S3Object(objectKey, content, contentType)
          }.toSet
      }

      InMemoryS3(
        objectsPerBucket.map {
          case (bucketName, objects) => InMemoryS3.Bucket(bucketName, objects)
        }.toSet
      )
    }

    final case class Bucket(name: S3BucketName, objects: Set[S3Object]) {
      def apply(key: S3ObjectKey): Option[S3Object] = objects.find(_.key == key)
      def hasObject(key: S3ObjectKey): Boolean = objects.exists(_.key == key)
      def addObject(s3Object: S3Object): Bucket = this.copy(objects = objects + s3Object)
    }

    final case class S3Object(key: S3ObjectKey, content: String, contentType: ContentType)
  }
}
