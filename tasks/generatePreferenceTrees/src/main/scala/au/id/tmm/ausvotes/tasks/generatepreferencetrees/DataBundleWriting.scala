package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import argonaut.Argonaut._
import au.id.tmm.ausvotes.core.model.codecs.CandidateCodec._
import au.id.tmm.ausvotes.core.model.codecs.GroupCodec._
import au.id.tmm.ausvotes.core.model.codecs.PartyCodec._
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.WritesToS3
import au.id.tmm.ausvotes.shared.aws.data.{ContentType, S3BucketName}
import au.id.tmm.ausvotes.shared.io.typeclasses.{BifunctorMonadError => BME}
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.{Parallel, SyncEffects}
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.ausvotes.tasks.generatepreferencetrees.DataBundleConstruction.DataBundleForElection
import au.id.tmm.countstv.model.preferences.PreferenceTreeSerialisation

object DataBundleWriting {

  def writeToS3Bucket[F[+_, +_] : WritesToS3 : Parallel : SyncEffects : BME](
                                                                                s3BucketName: S3BucketName,

                                                                                dataBundleForElection: DataBundleForElection,
                                                                              ): F[Exception, Unit] =
    for {
      _ <- Parallel.parAll(List(
        writePreferenceTree(s3BucketName, dataBundleForElection),
        writeGroupsFile(s3BucketName, dataBundleForElection),
        writeCandidatesFile(s3BucketName, dataBundleForElection),
        writeCanonicalRecountFile(s3BucketName, dataBundleForElection),
      ))
    } yield Unit

  private def writePreferenceTree[F[+_, +_] : WritesToS3 : SyncEffects : BME](
                                                                                 s3BucketName: S3BucketName,
                                                                                 dataBundleForElection: DataBundleForElection,
                                                                               ): F[Exception, Unit] = {
    val key = EntityLocations.locationOfPreferenceTree(dataBundleForElection.election, dataBundleForElection.state)

    WritesToS3.putFromOutputStream(s3BucketName, key) { outputStream =>
      SyncEffects.syncException {
        PreferenceTreeSerialisation.serialise[Candidate](dataBundleForElection.preferenceTree, outputStream)
      }
    }
  }

  private def writeGroupsFile[F[+_, +_] : WritesToS3 : BME](
                                                               s3BucketName: S3BucketName,
                                                               dataBundleForElection: DataBundleForElection,
                                                             ): F[Exception, Unit] = {
    val key = EntityLocations.locationOfGroupsObject(dataBundleForElection.election, dataBundleForElection.state)

    val content = {
      val groupsInOrder = dataBundleForElection.groupsAndCandidates.groups.toList.sortBy(_.index)

      groupsInOrder.asJson.toString
    }

    WritesToS3.putString(s3BucketName, key)(content, ContentType.APPLICATION_JSON)
  }

  private def writeCandidatesFile[F[+_, +_] : WritesToS3 : SyncEffects : BME](
                                                                                 s3BucketName: S3BucketName,
                                                                                 dataBundleForElection: DataBundleForElection,
                                                                               ): F[Exception, Unit] = {
    val key = EntityLocations.locationOfCandidatesObject(dataBundleForElection.election, dataBundleForElection.state)

    val content = {
      val candidatesInOrder = dataBundleForElection.groupsAndCandidates.candidates.toList.sortBy(_.btlPosition)

      candidatesInOrder.asJson.toString
    }

    WritesToS3.putString(s3BucketName, key)(content, ContentType.APPLICATION_JSON)
  }

  private def writeCanonicalRecountFile[F[+_, +_] : WritesToS3 : SyncEffects : BME](
                                                                                       s3BucketName: S3BucketName,
                                                                                       dataBundleForElection: DataBundleForElection,
                                                                                     ): F[Exception, Unit] = {
    val key = EntityLocations.locationOfCanonicalRecount(dataBundleForElection.election, dataBundleForElection.state)

    val content = dataBundleForElection.canonicalCountResult.asJson.toString

    WritesToS3.putJson(s3BucketName, key)(content)
  }

}
