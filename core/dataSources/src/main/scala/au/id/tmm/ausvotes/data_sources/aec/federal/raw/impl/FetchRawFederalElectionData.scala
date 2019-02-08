package au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl

import java.io.IOException
import java.net.{MalformedURLException, URL}
import java.nio.file.Path
import java.util.zip.ZipFile

import au.id.tmm.ausvotes.data_sources.aec.federal.raw.{FetchRawFederalPollingPlaces, FetchRawFormalSenatePreferences, FetchRawSenateDistributionOfPreferences, FetchRawSenateFirstPreferences}
import au.id.tmm.ausvotes.data_sources.common.Fs2Interop._
import au.id.tmm.ausvotes.data_sources.common.UrlUtils.StringOps
import au.id.tmm.ausvotes.data_sources.common.{CsvStreaming, DownloadToPath}
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.{SyncEffects, BifunctorMonadError => BME}
import au.id.tmm.utilities.io.FileUtils.ImprovedPath
import au.id.tmm.utilities.io.ZipFileUtils.ImprovedZipFile
import cats.effect.{Sync => CatsSync}
import fs2.Stream

import scala.io.Source

final class FetchRawFederalElectionData[F[+_, +_] : SyncEffects : BME] private (localStore: Path)(implicit downloadMethod: DownloadToPath[F])
  extends FetchRawSenateFirstPreferences[F]
    with FetchRawSenateDistributionOfPreferences[F]
    with FetchRawFederalPollingPlaces[F]
    with FetchRawFormalSenatePreferences[F] {

  override def senateFirstPreferencesFor(
                                          election: SenateElection,
                                        ): F[FetchRawSenateFirstPreferences.Error, Stream[F[Throwable, +?], FetchRawSenateFirstPreferences.Row]] =
    fetchStream(
      url = s"https://results.aec.gov.au/${election.federalElection.aecId.asInt}/Website/Downloads/SenateFirstPrefsByStateByVoteTypeDownload-${election.federalElection.aecId.asInt}.csv".parseUrl,
      targetPath = localStore.resolve(s"SenateFirstPrefsByStateByVoteTypeDownload-${election.federalElection.aecId.asInt}.csv"),
      makeRow = row =>
        FetchRawSenateFirstPreferences.Row(
          state = row(0),
          ticket = row(1),
          candidateId = row(2),
          positionInGroup = row(3).toInt,
          candidateDetails = row(4),
          party = row(5),
          ordinaryVotes = row(6).toInt,
          absentVotes = row(7).toInt,
          provisionalVotes = row(8).toInt,
          prePollVotes = row(9).toInt,
          postalVotes = row(10).toInt,
          totalVotes = row(10).toInt
        )
    ).leftMap(FetchRawSenateFirstPreferences.Error)

  override def federalPollingPlacesForElection(
                                                election: FederalElection,
                                              ): F[FetchRawFederalPollingPlaces.Error, Stream[F[Throwable, +?], FetchRawFederalPollingPlaces.Row]] =
    fetchStream(
      url = s"https://results.aec.gov.au/${election.aecId.asInt}/Website/Downloads/GeneralPollingPlacesDownload-${election.aecId.asInt}.csv".parseUrl,
      targetPath = localStore.resolve(s"GeneralPollingPlacesDownload-${election.aecId.asInt}.csv"),
      makeRow = row =>
        FetchRawFederalPollingPlaces.Row(
          state = row(0),
          divisionId = row(1).toInt,
          divisionName = row(2),
          pollingPlaceId = row(3).toInt,
          pollingPlaceTypeId = row(4).toInt,
          pollingPlaceName = row(5),
          premisesName = row(6),
          premisesAddress1 = row(7),
          premisesAddress2 = row(8),
          premisesAddress3 = row(9),
          premisesSuburb = row(10),
          premisesState = row(11),
          premisesPostcode = row(12),
          latitude = parsePossibleDouble(row(13)),
          longitude = parsePossibleDouble(row(14)),
        )
    ).leftMap(FetchRawFederalPollingPlaces.Error)

  override def senateDistributionOfPreferencesFor(
                                                   election: SenateElectionForState,
                                                 ): F[FetchRawSenateDistributionOfPreferences.Error, Stream[F[Throwable, +?], FetchRawSenateDistributionOfPreferences.Row]] =
    fetchZipBackedStream(
      url = s"https://results.aec.gov.au/${election.election.federalElection.aecId.asInt}/Website/External/SenateDopDownload-${election.election.federalElection.aecId.asInt}.zip".parseUrl,
      targetPath = localStore.resolve(s"SenateDopDownload-${election.election.federalElection.aecId.asInt}"),
      zipEntryName = s"SenateStateDOPDownload-${election.election.federalElection.aecId.asInt}-${election.state.abbreviation.toUpperCase}.csv",
      unsafeMakeRow = row =>
        FetchRawSenateDistributionOfPreferences.Row(
          state = row(0),
          numberOfVacancies = row(1).toInt,
          totalFormalPapers = row(2).toInt,
          quota = row(3).toInt,
          count = row(4).toInt,
          ballotPosition = row(5).toInt,
          ticket = row(6),
          surname = row(7),
          givenName = row(8),
          papers = row(9).toInt,
          votesTransferred = row(10).toInt,
          progressiveVoteTotal = row(11).toInt,
          transferValue = row(12).toDouble,
          status = row(13),
          changed = parsePossibleBoolean(row(14)),
          orderElected = row(15).toInt,
          comment = row(16),
        )
    ).leftMap(FetchRawSenateDistributionOfPreferences.Error)

  override def formalSenatePreferencesFor(
                                           election: SenateElectionForState,
                                         ): F[FetchRawFormalSenatePreferences.Error, Stream[F[Throwable, +?], FetchRawFormalSenatePreferences.Row]] =
    fetchZipBackedStream(
      url = s"https://results.aec.gov.au/${election.election.federalElection.aecId.asInt}/Website/External/aec-senate-formalpreferences-${election.election.federalElection.aecId.asInt}-${election.state.abbreviation}.zip".parseUrl,
      targetPath = localStore.resolve(s"aec-senate-formalpreferences-${election.election.federalElection.aecId.asInt}-${election.state.abbreviation}.zip"),
      zipEntryName = s"aec-senate-formalpreferences-${election.election.federalElection.aecId.asInt}-${election.state.abbreviation}.csv",
      unsafeMakeRow = row =>
        FetchRawFormalSenatePreferences.Row(
          electorateName = row(0),
          voteCollectionPointName = row(1),
          voteCollectionPointId = row(2).toInt,
          batchNumber = row(3).toInt,
          paperNumber = row(4).toInt,
          preferences = row(5),
        )
    ).leftMap(FetchRawFormalSenatePreferences.Error)

  private def fetchZipBackedStream[A](
                                       url: Either[MalformedURLException, URL],
                                       targetPath: => Path,
                                       zipEntryName: String,
                                       unsafeMakeRow: List[String] => A,
                                     ): F[Exception, Stream[F[Throwable, +?], A]] =
    for {
      theUrl <- BME.fromEither(url)
      theTargetPath <- SyncEffects.syncCatch(targetPath) {
        case e: IOException => e
      }

      _ <- DownloadToPath.downloadToPath(theUrl, theTargetPath)

      zipFile <- SyncEffects.syncCatch(new ZipFile(theTargetPath.toFile)) {
        case e: IOException => e
      }

      maybeZipEntry <- SyncEffects.syncException(zipFile.entryWithName(zipEntryName))

      zipEntry <- maybeZipEntry match {
        case Some(zipEntry) => BME.pure(zipEntry)
        case None => BME.leftPure(new NoSuchElementException(s"No zip entry called $zipEntryName"))
      }

      csvRowsStream = CsvStreaming.from[F[Throwable, +?]] {
        SyncEffects.syncException {
          Source.fromInputStream(zipFile.getInputStream(zipEntry), "UTF-8")
        }
      }
    } yield csvRowsStream.map(unsafeMakeRow)

  private def fetchStream[A](
                              url: Either[MalformedURLException, URL],
                              targetPath: => Path,
                              makeRow: List[String] => A,
                            ): F[Exception, Stream[F[Throwable, +?], A]] =
    for {
      theUrl <- BME.fromEither(url)
      theTargetPath <- SyncEffects.syncCatch(targetPath) {
        case e: IOException => e
      }

      _ <- DownloadToPath.downloadToPath(theUrl, theTargetPath)

      csvRowsStream = CsvStreaming.from[F[Throwable, +?]](SyncEffects.syncException(theTargetPath.source()))
    } yield csvRowsStream.map(makeRow)

  private def parsePossibleDouble(string: String): Option[Double] = {
    if (string.isEmpty) {
      None
    } else {
      Some(string.toDouble)
    }
  }

  private def parsePossibleBoolean(string: String): Option[Boolean] = {
    if (string.isEmpty) {
      None
    } else {
      Some(string.toBoolean)
    }
  }
}

object FetchRawFederalElectionData {
  def apply[F[+_, +_] : SyncEffects : BME](localStore: Path)(implicit downloadMethod: DownloadToPath[F], catsSync: CatsSync[F[Throwable, +?]]): FetchRawFederalElectionData[F] =
    new FetchRawFederalElectionData(localStore)
}
