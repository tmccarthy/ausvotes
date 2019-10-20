package au.id.tmm.ausvotes.shared.aws.actions

import java.io.{InputStream, OutputStream}

import au.id.tmm.ausvotes.shared.aws.data.{ContentType, LambdaFunctionName, S3BucketName, S3ObjectKey}
import au.id.tmm.ausvotes.shared.aws.{LambdaOps, S3Ops, SnsOps}
import zio.IO

object IOInstances {

  implicit val ioReadsS3: S3Actions.ReadsS3[IO] = new S3Actions.ReadsS3[IO] {
    override def readAsString(bucketName: S3BucketName, objectKey: S3ObjectKey): IO[Exception, String] =
      S3Ops.retrieveString(bucketName, objectKey)

    override def useInputStream[A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: InputStream => IO[Exception, A]): IO[Exception, A] =
      S3Ops.useInputStream(bucketName, objectKey)(use)

    override def checkObjectExists(bucketName: S3BucketName, objectKey: S3ObjectKey): IO[Exception, Boolean] =
      S3Ops.checkObjectExists(bucketName, objectKey)
  }

  implicit val ioWritesToS3: S3Actions.WritesToS3[IO] = new S3Actions.WritesToS3[IO] {
    override def putString(bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String, contentType: ContentType): IO[Exception, Unit] =
      S3Ops.putString(bucketName, objectKey, content, contentType)

    override def putFromOutputStream(bucketName: S3BucketName, objectKey: S3ObjectKey)(writeToOutputStream: OutputStream => IO[Exception, Unit]): IO[Exception, Unit] =
      S3Ops.putFromOutputStream(bucketName, objectKey)(writeToOutputStream)
  }

  implicit val ioPutsToSns: SnsActions.PutsSnsMessages[IO] = new SnsActions.PutsSnsMessages[IO] {
    override def putMessage(topicArn: String, messageBody: String): IO[Exception, Unit] =
      SnsOps.putMessage(topicArn, messageBody)
  }

  implicit val ioInvokesLambda: LambdaActions.InvokesLambda[IO] = new LambdaActions.InvokesLambda[IO] {
    override def invokeFunction(name: LambdaFunctionName, payload: Option[String]): IO[Exception, String] =
      LambdaOps.invokeLambda(name, payload)
  }

}
