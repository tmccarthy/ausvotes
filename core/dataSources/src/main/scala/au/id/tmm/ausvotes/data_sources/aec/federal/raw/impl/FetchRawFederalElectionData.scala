package au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl

import java.io.IOException
import java.net.{MalformedURLException, URL}
import java.nio.file.Path

import au.id.tmm.ausvotes.data_sources.aec.federal.raw.{FetchFederalPollingPlaces, FetchFormalSenatePreferences, FetchSenateDistributionOfPreferences, FetchSenateFirstPreferences}
import au.id.tmm.ausvotes.data_sources.common.UrlUtils.StringOps
import au.id.tmm.ausvotes.data_sources.common.{CsvStreaming, DownloadToPath}
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.io.FileUtils.ImprovedPath
import au.id.tmm.utilities.io.ZipFileUtils.{ImprovedPath, ImprovedZipFile}
import fs2.Stream
import scalaz.zio.IO
import scalaz.zio.interop.catz.taskEffectInstances

import scala.io.Source

final class FetchRawFederalElectionData private (localStore: Path)(implicit downloadMethod: DownloadToPath[IO])
  extends FetchSenateFirstPreferences[IO]
    with FetchSenateDistributionOfPreferences[IO]
    with FetchFederalPollingPlaces[IO]
    with FetchFormalSenatePreferences[IO] {

  override def senateFirstPreferencesFor(
                                          election: SenateElection,
                                        ): IO[FetchSenateFirstPreferences.Error, Stream[IO[Throwable, +?], FetchSenateFirstPreferences.Row]] =
    fetchStream(
      url = s"https://results.aec.gov.au/${election.federalElection.aecId.asInt}/Website/Downloads/SenateFirstPrefsByStateByVoteTypeDownload-${election.federalElection.aecId.asInt}.csv".parseUrl,
      targetPath = localStore.resolve(s"SenateFirstPrefsByStateByVoteTypeDownload-${election.federalElection.aecId.asInt}.csv"),
      makeRow = row =>
        FetchSenateFirstPreferences.Row(
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
    ).leftMap(FetchSenateFirstPreferences.Error)

  override def federalPollingPlacesForElection(
                                                election: FederalElection,
                                              ): IO[FetchFederalPollingPlaces.Error, Stream[IO[Throwable, +?], FetchFederalPollingPlaces.Row]] =
    fetchStream(
      url = s"https://results.aec.gov.au/${election.aecId.asInt}/Website/Downloads/GeneralPollingPlacesDownload-${election.aecId.asInt}.csv".parseUrl,
      targetPath = localStore.resolve(s"GeneralPollingPlacesDownload-${election.aecId.asInt}.csv"),
      makeRow = row =>
        FetchFederalPollingPlaces.Row(
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
    ).leftMap(FetchFederalPollingPlaces.Error)

  override def senateDistributionOfPreferencesFor(
                                                   election: SenateElectionForState,
                                                 ): IO[FetchSenateDistributionOfPreferences.Error, Stream[IO[Throwable, +?], FetchSenateDistributionOfPreferences.Row]] =
    fetchZipBackedStream(
      url = s"https://results.aec.gov.au/${election.election.federalElection.aecId.asInt}/Website/External/SenateDopDownload-${election.election.federalElection.aecId.asInt}.zip".parseUrl,
      targetPath = localStore.resolve(s"SenateDopDownload-${election.election.federalElection.aecId.asInt}"),
      zipEntryName = s"SenateStateDOPDownload-${election.election.federalElection.aecId.asInt}-${election.state.abbreviation.toUpperCase}.csv",
      unsafeMakeRow = row =>
        FetchSenateDistributionOfPreferences.Row(
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
    ).leftMap(FetchSenateDistributionOfPreferences.Error)

  override def formalSenatePreferencesFor(
                                           election: SenateElectionForState,
                                         ): IO[FetchFormalSenatePreferences.Error, Stream[IO[Throwable, +?], FetchFormalSenatePreferences.Row]] =
    fetchZipBackedStream(
      url = s"https://results.aec.gov.au/${election.election.federalElection.aecId.asInt}/Website/External/aec-senate-formalpreferences-${election.election.federalElection.aecId.asInt}-${election.state.abbreviation}.zip".parseUrl,
      targetPath = localStore.resolve(s"aec-senate-formalpreferences-${election.election.federalElection.aecId.asInt}-${election.state.abbreviation}.zip"),
      zipEntryName = s"aec-senate-formalpreferences-${election.election.federalElection.aecId.asInt}-${election.state.abbreviation}.csv",
      unsafeMakeRow = row =>
        FetchFormalSenatePreferences.Row(
          electorateName = row(0),
          voteCollectionPointName = row(1),
          voteCollectionPointId = row(2).toInt,
          batchNumber = row(3).toInt,
          paperNumber = row(4).toInt,
          preferences = row(5),
        )
    ).leftMap(FetchFormalSenatePreferences.Error)

  private def fetchZipBackedStream[A](
                                       url: Either[MalformedURLException, URL],
                                       targetPath: => Path,
                                       zipEntryName: String,
                                       unsafeMakeRow: List[String] => A,
                                     ): IO[Exception, Stream[IO[Throwable, +?], A]] =
    for {
      theUrl <- IO.fromEither(url)
      theTargetPath <- IO.syncCatch(targetPath) {
        case e: IOException => e
      }

      _ <- DownloadToPath.downloadToPath(theUrl, theTargetPath)

      zipFile <- IO.syncCatch(theTargetPath.asZipFile) {
        case e: IOException => e
      }

      maybeZipEntry <- IO.syncException(zipFile.entryWithName(zipEntryName))

      zipEntry <- maybeZipEntry match {
        case Some(zipEntry) => IO.point(zipEntry)
        case None => IO.fail(new NoSuchElementException(s"No zip entry called $zipEntryName"))
      }

      csvRowsStream = CsvStreaming.from[IO[Throwable, +?]] {
        IO.syncException {
          Source.fromInputStream(zipFile.getInputStream(zipEntry), "UTF-8")
        }
      }
    } yield csvRowsStream.map(unsafeMakeRow)

  private def fetchStream[A](
                              url: Either[MalformedURLException, URL],
                              targetPath: => Path,
                              makeRow: List[String] => A,
                            ): IO[Exception, Stream[IO[Throwable, +?], A]] =
    for {
      theUrl <- IO.fromEither(url)
      theTargetPath <- IO.syncCatch(targetPath) {
        case e: IOException => e
      }

      _ <- DownloadToPath.downloadToPath(theUrl, theTargetPath)

      csvRowsStream = CsvStreaming.from[IO[Throwable, +?]](IO.syncException(theTargetPath.source()))
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
  def apply(localStore: Path)(implicit downloadMethod: DownloadToPath[IO]): FetchRawFederalElectionData =
    new FetchRawFederalElectionData(localStore)
}
