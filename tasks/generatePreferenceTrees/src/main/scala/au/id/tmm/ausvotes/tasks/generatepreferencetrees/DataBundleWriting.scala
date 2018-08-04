package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import argonaut.Argonaut._
import argonaut.CodecJson
import au.id.tmm.ausvotes.core.model.codecs.{CandidateCodec, GroupCodec, PartyCodec}
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, CandidatePosition, Group}
import au.id.tmm.ausvotes.tasks.generatepreferencetrees.DataBundleConstruction.DataBundleForElection
import au.id.tmm.ausvotes.tasks.generatepreferencetrees.S3Utils.{S3BucketName, S3ObjectName}
import au.id.tmm.countstv.model.preferences.PreferenceTree.RootPreferenceTree
import au.id.tmm.countstv.model.preferences.PreferenceTreeSerialisation
import com.amazonaws.services.s3.AmazonS3
import scalaz.zio.IO

object DataBundleWriting {

  def writeToS3Bucket(
                       s3BucketName: S3BucketName,

                       dataBundleForElection: DataBundleForElection,
                     ): IO[Exception, Unit] = {

    implicit val partyCodec: PartyCodec = PartyCodec()
    implicit val groupCodec: GroupCodec = GroupCodec()
    implicit val candidateCodec: CodecJson[Candidate] = CandidateCodec(dataBundleForElection.groupsAndCandidates.groups)

    val outputDirectoryForThisWrite =
      S3ObjectName("recountData") / dataBundleForElection.election.id / dataBundleForElection.state.abbreviation

    for {
      s3Client <- S3Utils.constructClient

      _ <- IO.parAll(List(
        writePreferenceTree(s3Client, s3BucketName, outputDirectoryForThisWrite, dataBundleForElection.preferenceTree),
        writeGroupsFile(s3Client, s3BucketName, outputDirectoryForThisWrite, dataBundleForElection.groupsAndCandidates.groups),
        writeCandidatesFile(s3Client, s3BucketName, outputDirectoryForThisWrite, dataBundleForElection.groupsAndCandidates.candidates),
      ))

    } yield Unit

  }

  private def writePreferenceTree(
                                   s3Client: AmazonS3,
                                   s3BucketName: S3BucketName,
                                   directory: S3ObjectName,
                                   preferenceTree: RootPreferenceTree[CandidatePosition],
                                 ): IO[Exception, Unit] = {
    val key = directory / "preferences.tree"

    S3Utils.putFromOutputStream(s3Client, s3BucketName, key) { outputStream =>
      IO.syncException {
        PreferenceTreeSerialisation.serialise[CandidatePosition](preferenceTree, outputStream)
      }
    }
  }

  private def writeGroupsFile(
                               s3Client: AmazonS3,
                               s3BucketName: S3BucketName,
                               directory: S3ObjectName,
                               groups: Set[Group],
                             )(implicit groupCodec: GroupCodec): IO[Exception, Unit] = {
    val key = directory / "groups.json"
    val content = {
      val groupsInOrder = groups.toList.sortBy(_.index)

      groupsInOrder.asJson.toString
    }

    S3Utils.putString(s3Client, s3BucketName, key, content)
  }

  private def writeCandidatesFile(
                                   s3Client: AmazonS3,
                                   s3BucketName: S3BucketName,
                                   directory: S3ObjectName,
                                   candidates: Set[Candidate],
                                 )(implicit candidateCodec: CodecJson[Candidate]): IO[Exception, Unit] = {
    val key = directory / "candidates.json"
    val content = {
      val candidatesInOrder = candidates.toList.sortBy(_.btlPosition)

      candidatesInOrder.asJson.toString
    }

    S3Utils.putString(s3Client, s3BucketName, key, content)
  }

}
