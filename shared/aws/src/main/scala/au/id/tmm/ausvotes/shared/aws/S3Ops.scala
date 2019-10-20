package au.id.tmm.ausvotes.shared.aws

import java.io._
import java.nio.charset.Charset

import au.id.tmm.ausvotes.shared.aws.data.{ContentType, S3BucketName, S3ObjectKey}
import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.ziointerop._
import com.amazonaws.services.s3.model.{ObjectMetadata, S3Object}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import org.apache.commons.io.IOUtils
import zio.IO

object S3Ops {

  val charset: Charset = Charset.forName("UTF-8")

  private def s3Client: IO[Exception, AmazonS3] = Sync[IO].syncException(AmazonS3ClientBuilder.defaultClient())

  def retrieveString(bucketName: S3BucketName, objectKey: S3ObjectKey): IO[Exception, String] =
    useInputStream(bucketName, objectKey) { inputStream =>
      Sync[IO].syncException {
        IOUtils.toString(inputStream, charset)
      }
    }

  def useInputStream[A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: InputStream => IO[Exception, A]): IO[Exception, A] =
    useObject(bucketName, objectKey) { s3Object =>
      use(s3Object.getObjectContent)
    }

  def useObject[A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: S3Object => IO[Exception, A]): IO[Exception, A] = {
    val acquireS3Object = s3Client.flatMap[Any, Exception, S3Object] { client =>
      Sync[IO].syncException(client.getObject(bucketName.asString, objectKey.asString))
    }

    val closeS3Object: S3Object => IO[Nothing, Unit] = s3Object => IO.effectTotal(s3Object.close())

    IO.bracket(acquireS3Object)(closeS3Object)(use)
  }

  def putString(
                 bucketName: S3BucketName,
                 objectKey: S3ObjectKey,
                 content: String,
                 contentType: ContentType,
               ): IO[Exception, Unit] = s3Client.flatMap { client =>
    Sync[IO].syncException {
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

    val writeToSource = IO.bracket(Sync[IO].syncException(pipeSource))(os => IO.effectTotal(os.close()))(writeToOutputStream)

    val readFromSink = IO.bracket(Sync[IO].syncException(pipeSink))(is => IO.effectTotal(is.close())) { inputStream =>
      Sync[IO].syncException {
        client.putObject(bucketName.asString, objectKey.asString, inputStream, new ObjectMetadata())
      }
    }

    readFromSink.zipPar(writeToSource).map(_ => Unit)
  }

  def checkObjectExists(bucketName: S3BucketName, objectKey: S3ObjectKey): IO[Exception, Boolean] =
    s3Client.flatMap { client =>
      Sync[IO].syncException {
        client.doesObjectExist(bucketName.asString, objectKey.asString)
      }
    }

}
