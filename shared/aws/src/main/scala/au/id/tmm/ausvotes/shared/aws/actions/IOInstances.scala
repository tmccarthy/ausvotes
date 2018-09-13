package au.id.tmm.ausvotes.shared.aws.actions

import java.io.{InputStream, OutputStream}

import au.id.tmm.ausvotes.shared.aws.{S3BucketName, S3ObjectKey, S3Ops}
import scalaz.zio.IO

object IOInstances {

  implicit val ioReadsS3: S3Actions.ReadsS3[IO] = new S3Actions.ReadsS3[IO] {
    override def readAsString(bucketName: S3BucketName, objectKey: S3ObjectKey): IO[Exception, String] =
      S3Ops.retrieveString(bucketName, objectKey)

    override def useInputStream[A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: InputStream => IO[Exception, A]): IO[Exception, A] =
      S3Ops.useInputStream(bucketName, objectKey)(use)
  }

  implicit val ioWritesToS3: S3Actions.WritesToS3[IO] = new S3Actions.WritesToS3[IO] {
    override def putString(bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String): IO[Exception, Unit] =
      S3Ops.putString(bucketName, objectKey, content)

    override def putFromOutputStream(bucketName: S3BucketName, objectKey: S3ObjectKey)(writeToOutputStream: OutputStream => IO[Exception, Unit]): IO[Exception, Unit] =
      S3Ops.putFromOutputStream(bucketName, objectKey)(writeToOutputStream)
  }

}
