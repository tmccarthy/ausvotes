package au.id.tmm.ausvotes.shared.aws

import java.io.{InputStream, OutputStream, PipedInputStream, PipedOutputStream}
import java.net.URL
import java.nio.charset.Charset

import com.amazonaws.services.s3.model.{ObjectMetadata, S3Object}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import org.apache.commons.io.IOUtils
import scalaz.zio.IO

object S3Ops {

  private def s3Client: IO[Exception, AmazonS3] = IO.syncException(AmazonS3ClientBuilder.defaultClient())

  def retrieveString(bucketName: S3BucketName, objectKey: S3ObjectKey): IO[Exception, String] =
    useInputStream(bucketName, objectKey) { inputStream =>
      IO.syncException {
        IOUtils.toString(inputStream, Charset.forName("UTF-8"))
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
               ): IO[Exception, Unit] = s3Client.flatMap { client =>
    IO.syncException {
      client.putObject(bucketName.asString, objectKey.asString, content)
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

  def objectUrl(region: String, bucketName: S3BucketName, objectKey: S3ObjectKey): URL =
    new URL(s"https://s3-$region.amazonaws.com/${bucketName.asString}/${objectKey.asString}")
}
