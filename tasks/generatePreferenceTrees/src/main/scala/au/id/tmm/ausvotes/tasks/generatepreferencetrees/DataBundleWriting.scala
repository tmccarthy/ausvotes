package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import argonaut.Argonaut._
import au.id.tmm.ausvotes.core.model.codecs.CandidateCodec._
import au.id.tmm.ausvotes.core.model.codecs.GroupCodec._
import au.id.tmm.ausvotes.core.model.codecs.PartyCodec._
import au.id.tmm.ausvotes.core.model.parsing.CandidatePosition
import au.id.tmm.ausvotes.shared.aws.S3Ops
import au.id.tmm.ausvotes.shared.aws.data.{ContentType, S3BucketName}
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.ausvotes.tasks.generatepreferencetrees.DataBundleConstruction.DataBundleForElection
import au.id.tmm.countstv.model.preferences.PreferenceTreeSerialisation
import scalaz.zio.IO

object DataBundleWriting {

  def writeToS3Bucket(
                       s3BucketName: S3BucketName,

                       dataBundleForElection: DataBundleForElection,
                     ): IO[Exception, Unit] = {

    for {
      _ <- IO.parAll(List(
        writePreferenceTree(s3BucketName, dataBundleForElection),
        writeGroupsFile(s3BucketName, dataBundleForElection),
        writeCandidatesFile(s3BucketName, dataBundleForElection),
      ))
    } yield Unit

  }

  private def writePreferenceTree(
                                   s3BucketName: S3BucketName,
                                   dataBundleForElection: DataBundleForElection,
                                 ): IO[Exception, Unit] = {
    val key = EntityLocations.locationOfPreferenceTree(dataBundleForElection.election, dataBundleForElection.state)

    S3Ops.putFromOutputStream(s3BucketName, key) { outputStream =>
      IO.syncException {
        PreferenceTreeSerialisation.serialise[CandidatePosition](dataBundleForElection.preferenceTree, outputStream)
      }
    }
  }

  private def writeGroupsFile(
                               s3BucketName: S3BucketName,
                               dataBundleForElection: DataBundleForElection,
                             ): IO[Exception, Unit] = {
    val key = EntityLocations.locationOfGroupsObject(dataBundleForElection.election, dataBundleForElection.state)

    val content = {
      val groupsInOrder = dataBundleForElection.groupsAndCandidates.groups.toList.sortBy(_.index)

      groupsInOrder.asJson.toString
    }

    S3Ops.putString(s3BucketName, key, content, ContentType.APPLICATION_JSON)
  }

  private def writeCandidatesFile(
                                   s3BucketName: S3BucketName,
                                   dataBundleForElection: DataBundleForElection,
                                 ): IO[Exception, Unit] = {
    val key = EntityLocations.locationOfCandidatesObject(dataBundleForElection.election, dataBundleForElection.state)

    val content = {
      val candidatesInOrder = dataBundleForElection.groupsAndCandidates.candidates.toList.sortBy(_.btlPosition)

      candidatesInOrder.asJson.toString
    }

    S3Ops.putString(s3BucketName, key, content, ContentType.APPLICATION_JSON)
  }

}
