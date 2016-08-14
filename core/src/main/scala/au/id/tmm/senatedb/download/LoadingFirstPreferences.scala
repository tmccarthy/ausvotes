package au.id.tmm.senatedb.download

import java.nio.file.{Files, Path}

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.option.OptionUtils.ImprovedOption

import scala.io.Source
import scala.util.{Failure, Try}

// Note that we don't perform a data integrity check here because this file is generated dynamically on download, and
// has a timestamp
object LoadingFirstPreferences {

  def csvLinesOf(dataDir: Path, election: SenateElection,
                 shouldDownloadIfNeeded: Boolean = true): Try[Source] = {
    for {
      matchingResource <- resourceMatching(election)
      localRawDataFile <- findRawDataFor(dataDir, matchingResource, shouldDownloadIfNeeded)
      source <- Try(Source.fromFile(localRawDataFile.toFile))
    } yield source
  }

  private def resourceMatching(election: SenateElection): Try[FirstPreferencesResource] =
    FirstPreferencesResource.of(election)
      .failIfAbsent(new UnsupportedOperationException(s"Could not find raw first preferences data for $election"))

  private def findRawDataFor(dataDir: Path, resource: FirstPreferencesResource, shouldDownloadIfNeeded: Boolean): Try[Path] = {
    val expectedPath = expectedLocalPathOf(dataDir, resource)

    if (Files.exists(expectedPath)) {
      Try(expectedPath)

    } else if (shouldDownloadIfNeeded) {
      downloadRawDataFor(resource, expectedPath)
        .map(_ => expectedPath)

    } else {
      Failure(new IllegalStateException(s"Raw data for ${resource.election} at " +
        s"${resource.election} has not been downloaded"))

    }
  }

  private def expectedLocalPathOf(dataDir: Path, resource: FirstPreferencesResource): Path = dataDir.
    resolve(resource.localFileName)

  private def downloadRawDataFor(resource: FirstPreferencesResource, target: Path): Try[Unit] =
    downloadUrlToFile(resource.url, target)
}
