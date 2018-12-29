package au.id.tmm.ausvotes.core.rawdata.download

import java.io.InputStream
import java.nio.file.Path
import java.util.zip.{ZipEntry, ZipFile}

import au.id.tmm.ausvotes.core.rawdata.resources.FormalPreferencesResource
import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import au.id.tmm.utilities.io.ZipFileUtils.{ImprovedPath, ImprovedZipFile}
import au.id.tmm.utilities.option.OptionUtils.ImprovedOption

import scala.io.Source
import scala.util.Try

object LoadingFormalPreferences {

  def csvLinesOf(matchingResource: FormalPreferencesResource, dataFilePath: Path): Try[Source] = {
    for {
      inputStream <- csvInputStreamFrom(matchingResource, dataFilePath)
      source <- Try(Source.fromInputStream(inputStream, "UTF-8"))
    } yield source
  }

  def resourceMatching(election: SenateElectionForState): Try[FormalPreferencesResource] =
    FormalPreferencesResource.of(election)
      .failIfAbsent(new UnsupportedOperationException(s"Could not find raw data for $election"))

  private def csvInputStreamFrom(resource: FormalPreferencesResource, zipFilePath: Path): Try[InputStream] =
    for {
      zipFile <- Try(zipFilePath.asZipFile)
      zipEntry <- findMatchingZipEntry(resource, zipFilePath, zipFile)
      inputStream <- Try(zipFile.getInputStream(zipEntry))
    } yield inputStream

  private def findMatchingZipEntry(resource: FormalPreferencesResource,
                                   zipFilePath: Path,
                                   zipFile: ZipFile): Try[ZipEntry] = {
    zipFile
      .entryWithName(resource.zipEntryName)
      .failIfAbsent(throw new IllegalStateException(s"Could not find expected zip file '${resource.zipEntryName}'" +
        s" in file at $zipFilePath"))
  }
}
