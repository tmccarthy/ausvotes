package au.id.tmm.ausvotes.shared.aws.actions

import java.io.{InputStream, OutputStream}

import au.id.tmm.ausvotes.shared.aws.{S3BucketName, S3ObjectKey}

object S3Actions {

  abstract class ReadsS3[F[+_, +_]] {
    def readAsString(bucketName: S3BucketName, objectKey: S3ObjectKey): F[Exception, String]
    def useInputStream[A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: InputStream => F[Exception, A]): F[Exception, A]
  }

  object ReadsS3 {
    def readAsString[F[+_, +_] : ReadsS3](bucketName: S3BucketName, objectKey: S3ObjectKey): F[Exception, String] =
      implicitly[ReadsS3[F]].readAsString(bucketName, objectKey)

    def useInputStream[F[+_, +_] : ReadsS3, A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: InputStream => F[Exception, A]): F[Exception, A] =
      implicitly[ReadsS3[F]].useInputStream(bucketName, objectKey)(use)
  }

  abstract class WritesToS3[F[+_, +_]] {
    def putString(bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String): F[Exception, Unit]
    def putFromOutputStream(bucketName: S3BucketName, objectKey: S3ObjectKey)(writeToOutputStream: OutputStream => F[Exception, Unit]): F[Exception, Unit]
  }

  object WritesToS3 {
    def putString[F[+_, +_] : WritesToS3](bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String): F[Exception, Unit] =
      implicitly[WritesToS3[F]].putString(bucketName, objectKey)(content)
    def putFromOutputStream[F[+_, +_] : WritesToS3](bucketName: S3BucketName, objectKey: S3ObjectKey)(writeToOutputStream: OutputStream => F[Exception, Unit]): F[Exception, Unit] =
      implicitly[WritesToS3[F]].putFromOutputStream(bucketName, objectKey)(writeToOutputStream)
  }

}
