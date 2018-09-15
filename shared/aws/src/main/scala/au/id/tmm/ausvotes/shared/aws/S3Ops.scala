package au.id.tmm.ausvotes.shared.aws

import java.io._
import java.nio.charset.Charset

import au.id.tmm.ausvotes.shared.aws.data.{ContentType, S3BucketName, S3ObjectKey}
import com.amazonaws.services.s3.model.{ObjectMetadata, S3Object}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import org.apache.commons.io.IOUtils
import scalaz.zio.IO

object S3Ops {

  val charset: Charset = Charset.forName("UTF-8")

  private def s3Client: IO[Exception, AmazonS3] = IO.syncException(AmazonS3ClientBuilder.defaultClient())

  def retrieveString(bucketName: S3BucketName, objectKey: S3ObjectKey): IO[Exception, String] =
    useInputStream(bucketName, objectKey) { inputStream =>
      IO.syncException {
        IOUtils.toString(inputStream, charset)
      }
    }

  def useInputStream[A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: InputStream => IO[Exception, A]): IO[Exception, A] =
    useObject(bucketName, objectKey) { s3Object =>
      use(s3Object.getObjectContent)
    }

  def useObject[A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: S3Object => IO[Exception, A]): IO[Exception, A] = {
    val acquireS3Object = s3Client.map { client =>
      client.getObject(bucketName.asString, objectKey.asString)
    }

    val closeS3Object: S3Object => IO[Nothing, Unit] = s3Object => IO.sync(s3Object.close())

    IO.bracket(acquireS3Object)(closeS3Object)(use)
  }

  def putString(
                 bucketName: S3BucketName,
                 objectKey: S3ObjectKey,
                 content: String,
                 contentType: ContentType,
               ): IO[Exception, Unit] = s3Client.flatMap { client =>
    IO.syncException {
      val bytes = content.getBytes(charset)
      val stream = new ByteArrayInputStream(bytes)

      val metadata = new ObjectMetadata()

      metadata.setContentType(contentType.asString)
      metadata.setContentLength(bytes.length)
      metadata.setContentEncoding(charset.name)

      client.putObject(bucketName.asString, objectKey.asString, stream, metadata)
    }
  }

  def putFromOutputStream(
                           bucketName: S3BucketName,
                           objectKey: S3ObjectKey,
                         )(
                           writeToOutputStream: OutputStream => IO[Exception, Unit],
                         ): IO[Exception, Unit] = s3Client.flatMap { client =>
    val pipeSource = new PipedOutputStream()
    val pipeSink = new PipedInputStream(pipeSource)

    val writeToSource = IO.bracket(IO.syncException(pipeSource))(os => IO.sync(os.close()))(writeToOutputStream)

    val readFromSink = IO.bracket(IO.syncException(pipeSink))(is => IO.sync(is.close())) { inputStream =>
      IO.syncException {
        client.putObject(bucketName.asString, objectKey.asString, inputStream, new ObjectMetadata())
      }
    }

    (readFromSink par writeToSource).map(_ => Unit)
  }

  def checkObjectExists(bucketName: S3BucketName, objectKey: S3ObjectKey): IO[Exception, Boolean] =
    s3Client.flatMap { client =>
      IO.syncException {
        client.doesObjectExist(bucketName.asString, objectKey.asString)
      }
    }

}
