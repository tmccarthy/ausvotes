package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import java.nio.file.{InvalidPathException, Path, Paths}

import au.id.tmm.ausvotes.core.model.SenateElection

import scala.reflect.ClassTag

final case class Args(
                       dataStorePath: Path,
                       outputPath: Path,
                       election: SenateElection,
                     )

object Args {

  def from(rawArgs: List[String]): Either[IllegalArgumentException, Args] = {
    for {
      _ <- if (rawArgs.length == 3) Right(Unit) else Left(new IllegalArgumentException("Incorrect number of arguments"))
      dataStorePath <- parsePath(rawArgs(0))
      outputPath <- parsePath(rawArgs(1))
      electionId = rawArgs(2)
      election <- SenateElection.forId(electionId).toRight(new IllegalArgumentException(s"Bad election id $electionId"))
    } yield Args(
      dataStorePath,
      outputPath,
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
}