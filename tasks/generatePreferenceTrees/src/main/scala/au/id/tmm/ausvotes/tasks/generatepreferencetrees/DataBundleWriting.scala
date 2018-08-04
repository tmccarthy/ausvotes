package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import java.nio.file.{Files, Path}

import argonaut.Argonaut._
import argonaut.CodecJson
import au.id.tmm.ausvotes.core.model.codecs.{CandidateCodec, GroupCodec, PartyCodec}
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, CandidatePosition, Group}
import au.id.tmm.ausvotes.tasks.generatepreferencetrees.Args.S3BucketName
import au.id.tmm.ausvotes.tasks.generatepreferencetrees.DataBundleConstruction.DataBundleForElection
import au.id.tmm.countstv.model.preferences.PreferenceTree.RootPreferenceTree
import au.id.tmm.countstv.model.preferences.PreferenceTreeSerialisation
import org.apache.commons.io.FileUtils
import scalaz.zio.IO

object DataBundleWriting {

  def writeToS3Bucket(
                       s3BucketName: S3BucketName,

                       dataBundleForElection: DataBundleForElection,
                     ): IO[Exception, Unit] = {

    implicit val partyCodec: PartyCodec = PartyCodec()
    implicit val groupCodec: GroupCodec = GroupCodec()
    implicit val candidateCodec: CodecJson[Candidate] = CandidateCodec(dataBundleForElection.groupsAndCandidates.groups)

    // TODO implement this properly

    for {
      outputPath <- IO.syncException(Files.createTempDirectory("recount_data"))

      outputDirectoryForThisWrite = outputPath
        .resolve(dataBundleForElection.election.id)
        .resolve(dataBundleForElection.state.abbreviation)

      _ <- IO.syncException(FileUtils.deleteDirectory(outputDirectoryForThisWrite.toFile))
      _ <- IO.syncException(Files.createDirectories(outputDirectoryForThisWrite))

      _ <- IO.parAll(List(
        writePreferenceTree(outputDirectoryForThisWrite, dataBundleForElection.preferenceTree),
        writeGroupsFile(outputDirectoryForThisWrite, dataBundleForElection.groupsAndCandidates.groups),
        writeCandidatesFile(outputDirectoryForThisWrite, dataBundleForElection.groupsAndCandidates.candidates),
      ))

    } yield Unit

  }

  private def writePreferenceTree(directory: Path, preferenceTree: RootPreferenceTree[CandidatePosition]): IO[Exception, Unit] = {
    for {
      file <- IO.syncException(directory.resolve("preferences.tree"))

      _ <- CloseableIO.bracket(CloseableIO.outputStreamFor(file)) { outputStream =>
        IO.syncException(PreferenceTreeSerialisation.serialise[CandidatePosition](preferenceTree, outputStream))
      }
    } yield Unit
  }

  private def writeGroupsFile(directory: Path, groups: Set[Group])(implicit groupCodec: GroupCodec): IO[Exception, Unit] = {
    for {
      file <- IO.syncException(directory.resolve("groups.json"))

      _ <- CloseableIO.bracket(CloseableIO.outputStreamFor(file)) { outputStream =>
        IO.syncException {
          val groupsInOrder = groups.toList.sortBy(_.index)
          val groupsJson = groupsInOrder.asJson.toString

          Files.write(file, java.util.Collections.singleton(groupsJson))
        }
      }
    } yield Unit
  }

  private def writeCandidatesFile(directory: Path, candidates: Set[Candidate])(implicit candidateCodec: CodecJson[Candidate]): IO[Exception, Unit] = {
    for {
      file <- IO.syncException(directory.resolve("groups.json"))

      _ <- CloseableIO.bracket(CloseableIO.outputStreamFor(file)) { outputStream =>
        IO.syncException {
          val candidatesInOrder = candidates.toList.sortBy(_.btlPosition)

          val candidatesJson = candidatesInOrder.asJson.toString

          Files.write(file, java.util.Collections.singleton(candidatesJson))
        }
      }
    } yield Unit
  }

}
