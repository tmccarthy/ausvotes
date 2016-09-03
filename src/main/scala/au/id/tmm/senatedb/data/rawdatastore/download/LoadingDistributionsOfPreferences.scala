package au.id.tmm.senatedb.data.rawdatastore.download

import java.io.InputStream
import java.nio.file.{Files, Path}
import java.util.zip.{ZipEntry, ZipFile}

import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.io.ZipFileUtils.{ImprovedPath, ImprovedZipFile}
import au.id.tmm.utilities.option.OptionUtils.ImprovedOption

import scala.io.Source
import scala.util.{Failure, Try}

private[rawdatastore] object LoadingDistributionsOfPreferences {

  def csvLinesOf(dataDir: Path, election: SenateElection, state: State,
                 shouldDownloadIfNeeded: Boolean = true): Try[Source] = {
    for {
      matchingResource <- resourceMatching(election)
      dataFilePath <- findRawDataFor(dataDir, matchingResource, shouldDownloadIfNeeded)
      inputStream <- csvInputStreamFrom(matchingResource, dataFilePath, state)
      source <- Try(Source.fromInputStream(inputStream, "UTF-8"))
    } yield source
  }

  private def resourceMatching(election: SenateElection): Try[DistributionOfPreferencesResource] =
    DistributionOfPreferencesResource.of(election)
      .failIfAbsent(new UnsupportedOperationException(s"Could not find a distribution of preferences for $election"))

  private def findRawDataFor(dataDir: Path, resource: DistributionOfPreferencesResource,
                             shouldDownloadIfNeeded: Boolean): Try[Path] = {
    val expectedPath = dataDir.resolve(resource.localFilePath)

    if (Files.exists(expectedPath)) {
      localResourceIntegrityCheck(expectedPath, resource.digest).map(_ => expectedPath)

    } else if (shouldDownloadIfNeeded) {
      downloadUrlToFile(resource.url, expectedPath)
        .flatMap(_ => localResourceIntegrityCheck(expectedPath, resource.digest))
        .map(_ => expectedPath)

    } else {
      Failure(new DataMissingDownloadDisallowedException(resource.url))

    }
  }

  private def csvInputStreamFrom(resource: DistributionOfPreferencesResource, zipFilePath: Path, state: State): Try[InputStream] =
    for {
      zipFile <- Try(zipFilePath.asZipFile)
      zipEntry <- findMatchingZipEntry(resource, state, zipFilePath, zipFile)
      inputStream <- Try(zipFile.getInputStream(zipEntry))
    } yield inputStream

  private def findMatchingZipEntry(resource: DistributionOfPreferencesResource,
                                   state: State,
                                   zipFilePath: Path,
                                   zipFile: ZipFile): Try[ZipEntry] = {
    val zipEntryName = resource.zipEntryNameOf(state)

    zipFile
      .entryWithName(zipEntryName)
      .failIfAbsent(throw new IllegalStateException(s"Could not find expected zip file '${zipEntryName}'" +
        s" in file at $zipFilePath"))
  }
}
