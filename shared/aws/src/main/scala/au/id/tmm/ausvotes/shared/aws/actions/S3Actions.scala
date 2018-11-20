package au.id.tmm.ausvotes.shared.aws.actions

import java.io.{InputStream, OutputStream}

import au.id.tmm.ausvotes.shared.aws.data.{ContentType, S3BucketName, S3ObjectKey}

object S3Actions {

  // TODO change the method signatures here to be more clear about the exception types
  trait ReadsS3[F[+_, +_]] {
    def readAsString(bucketName: S3BucketName, objectKey: S3ObjectKey): F[Exception, String]
    def useInputStream[A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: InputStream => F[Exception, A]): F[Exception, A]
    def checkObjectExists(bucketName: S3BucketName, objectKey: S3ObjectKey): F[Exception, Boolean]
  }

  object ReadsS3 {
    def readAsString[F[+_, +_] : ReadsS3](bucketName: S3BucketName, objectKey: S3ObjectKey): F[Exception, String] =
      implicitly[ReadsS3[F]].readAsString(bucketName, objectKey)

    def useInputStream[F[+_, +_] : ReadsS3, A](bucketName: S3BucketName, objectKey: S3ObjectKey)(use: InputStream => F[Exception, A]): F[Exception, A] =
      implicitly[ReadsS3[F]].useInputStream(bucketName, objectKey)(use)

    def checkObjectExists[F[+_, +_] : ReadsS3](bucketName: S3BucketName, objectKey: S3ObjectKey): F[Exception, Boolean] =
      implicitly[ReadsS3[F]].checkObjectExists(bucketName, objectKey)
  }

  trait WritesToS3[F[+_, +_]] {
    def putString(bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String, contentType: ContentType): F[Exception, Unit]

    def putText(bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String): F[Exception, Unit] =
      putString(bucketName, objectKey)(content, contentType = ContentType.TEXT_PLAIN)

    def putJson(bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String): F[Exception, Unit] =
      putString(bucketName, objectKey)(content, contentType = ContentType.APPLICATION_JSON)

    def putFromOutputStream(bucketName: S3BucketName, objectKey: S3ObjectKey)(writeToOutputStream: OutputStream => F[Exception, Unit]): F[Exception, Unit]
  }

  object WritesToS3 {
    def putString[F[+_, +_] : WritesToS3](bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String, contentType: ContentType): F[Exception, Unit] =
      implicitly[WritesToS3[F]].putString(bucketName, objectKey)(content, contentType)

    def putText[F[+_, +_] : WritesToS3](bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String): F[Exception, Unit] =
      implicitly[WritesToS3[F]].putText(bucketName, objectKey)(content)

    def putJson[F[+_, +_] : WritesToS3](bucketName: S3BucketName, objectKey: S3ObjectKey)(content: String): F[Exception, Unit] =
      implicitly[WritesToS3[F]].putJson(bucketName, objectKey)(content)

    def putFromOutputStream[F[+_, +_] : WritesToS3](bucketName: S3BucketName, objectKey: S3ObjectKey)(writeToOutputStream: OutputStream => F[Exception, Unit]): F[Exception, Unit] =
      implicitly[WritesToS3[F]].putFromOutputStream(bucketName, objectKey)(writeToOutputStream)
  }

}
