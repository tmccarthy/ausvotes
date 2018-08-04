package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import java.nio.file.{InvalidPathException, Path, Paths}

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.tasks.generatepreferencetrees.Args.S3BucketName

import scala.reflect.ClassTag

final case class Args(
                       dataStorePath: Path,
                       s3Bucket: S3BucketName,
                       election: SenateElection,
                     )

object Args {

  def from(rawArgs: List[String]): Either[IllegalArgumentException, Args] = {
    for {
      _ <- if (rawArgs.length == 3) Right(Unit) else Left(new IllegalArgumentException("Incorrect number of arguments"))
      dataStorePath <- parsePath(rawArgs(0))
      s3Bucket = S3BucketName(rawArgs(1))
      electionId = rawArgs(2)
      election <- SenateElection.forId(electionId).toRight(new IllegalArgumentException(s"Bad election id $electionId"))
    } yield Args(
      dataStorePath,
      s3Bucket,
      election,
    )
  }

  private def parsePath(rawPath: String): Either[InvalidPathException, Path] =
    narrowTry[Path, InvalidPathException](Paths.get(rawPath))

  private def narrowTry[A, E <: Exception : ClassTag](block: => A): Either[E, A] = {
    try {
      Right(block)
    } catch {
      case e: E => Left(e)
    }
  }

  final case class S3BucketName(string: String) extends AnyVal

}