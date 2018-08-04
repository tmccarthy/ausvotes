package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import java.io._

import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import scalaz.zio.IO

object S3Utils {

  final case class S3BucketName(asString: String) extends AnyVal

  final case class S3ObjectName(elements: List[String]) {
    def /(key: String): S3ObjectName = S3ObjectName(elements :+ key)

    def asString: String = elements.mkString("/")
  }

  object S3ObjectName {
    def apply(key: String): S3ObjectName = S3ObjectName(List(key))
  }

  def constructClient: IO[Exception, AmazonS3] = IO.syncException(AmazonS3ClientBuilder.defaultClient)

  def putString(
                 client: AmazonS3,
                 bucketName: S3BucketName,
                 key: S3ObjectName,
                 content: String,
               ): IO[Exception, Unit] = IO.syncException {
    client.putObject(bucketName.asString, key.asString, content)
  }

  def putFromOutputStream(
                           client: AmazonS3,
                           bucketName: S3BucketName,
                           key: S3ObjectName,
                         )(
                           writeToOutputStream: OutputStream => IO[Exception, Unit],
                         ): IO[Exception, Unit] = {
    val pipeSource = new PipedOutputStream()
    val pipeSink = new PipedInputStream(pipeSource)

    val writeToSource = CloseableIO.bracket(IO.syncException(pipeSource))(writeToOutputStream)

    val readFromSink = CloseableIO.bracket(IO.syncException(pipeSink)) { inputStream =>
      IO.syncException {
        client.putObject(bucketName.asString, key.asString, inputStream, new ObjectMetadata())
      }
    }

    (readFromSink par writeToSource).map(_ => Unit)
  }

}
