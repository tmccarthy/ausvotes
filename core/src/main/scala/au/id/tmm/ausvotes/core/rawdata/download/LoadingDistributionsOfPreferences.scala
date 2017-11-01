package au.id.tmm.ausvotes.core.rawdata.download

import java.nio.file.Path
import java.util.zip.{ZipEntry, ZipFile}

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.rawdata.resources.DistributionOfPreferencesResource
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.io.ZipFileUtils.{ImprovedPath, ImprovedZipFile}
import au.id.tmm.utilities.option.OptionUtils.ImprovedOption

import scala.io.Source
import scala.util.Try

object LoadingDistributionsOfPreferences {

  def csvLinesFor(resource: DistributionOfPreferencesResource, resourceLocation: Path, state: State): Try[Source] = {
    for {
      zipFile <- Try(resourceLocation.asZipFile)
      zipEntry <- findMatchingZipEntry(resource, state, resourceLocation, zipFile)
      inputStream <- Try(zipFile.getInputStream(zipEntry))
      source <- Try(Source.fromInputStream(inputStream, "UTF-8"))
    } yield source
  }

  def resourceMatching(election: SenateElection): Try[DistributionOfPreferencesResource] =
    DistributionOfPreferencesResource.of(election)
      .failIfAbsent(new UnsupportedOperationException(s"Could not find a distribution of preferences for $election"))

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
