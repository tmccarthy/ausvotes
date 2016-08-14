package au.id.tmm.senatedb.download

import java.io.InputStream
import java.nio.file.{Files, Path}
import java.util.zip.{ZipEntry, ZipFile}

import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.option.OptionUtils.ImprovedOption

import scala.io.Source
import scala.util.{Failure, Try}

object LoadingFormalPreferences {

  def csvLinesOf(dataDir: Path, election: SenateElection, state: State,
                 shouldDownloadIfNeeded: Boolean = true): Try[Source] = {
    for {
      matchingResource <- resourceMatching(election, state)
      dataFilePath <- findRawDataFor(dataDir, matchingResource, shouldDownloadIfNeeded)
      inputStream <- csvInputStreamFrom(matchingResource, dataFilePath)
      source <- Try(Source.fromInputStream(inputStream, "UTF-8"))
    } yield source
  }

  private def resourceMatching(election: SenateElection, state: State): Try[FormalPreferencesResource] =
    FormalPreferencesResource.of(election, state)
      .failIfAbsent(new UnsupportedOperationException(s"Could not find raw data for $state at $election"))

  private def findRawDataFor(dataDir: Path, resource: FormalPreferencesResource,
                             shouldDownloadIfNeeded: Boolean): Try[Path] = {
    val expectedPath = expectedLocalPathOf(dataDir, resource)

    if (Files.exists(expectedPath)) {
      localResourceIntegrityCheck(expectedPath, resource.digest).map(_ => expectedPath)

    } else if (shouldDownloadIfNeeded) {
      downloadRawDataFor(resource, expectedPath)
        .flatMap(_ => localResourceIntegrityCheck(expectedPath, resource.digest))
        .map(_ => expectedPath)

    } else {
      Failure(new IllegalStateException(s"Raw data for ${resource.state} at " +
        s"${resource.election} has not been downloaded"))

    }
  }

  private def expectedLocalPathOf(dataDir: Path, resource: FormalPreferencesResource): Path = dataDir
    .resolve(resource.localFileName)

  private def downloadRawDataFor(resource: FormalPreferencesResource, target: Path): Try[Unit] =
    downloadUrlToFile(resource.url, target)

  private def csvInputStreamFrom(resource: FormalPreferencesResource, zipFilePath: Path): Try[InputStream] =
    for {
      zipFile <- Try(new ZipFile(zipFilePath.toFile, ZipFile.OPEN_READ))
      zipEntry <- findMatchingZipEntry(resource, zipFilePath, zipFile)
      inputStream <- Try(zipFile.getInputStream(zipEntry))
    } yield inputStream

  private def findMatchingZipEntry(resource: FormalPreferencesResource,
                                   zipFilePath: Path,
                                   zipFile: ZipFile): Try[ZipEntry] = {
    import collection.JavaConverters._

    zipFile.entries()
      .asScala
      .find(entry => entry.getName == resource.zipEntryName)
      .failIfAbsent(throw new IllegalStateException(s"Could not find expected zip file '${resource.zipEntryName}'" +
        s" in file at $zipFilePath"))
  }
}
