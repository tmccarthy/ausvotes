package au.id.tmm.senatedb.rawdata.download

import java.io.InputStream
import java.nio.file.Path
import java.util.zip.{ZipEntry, ZipFile}

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.download.StorageUtils.findRawDataWithIntegrityCheckFor
import au.id.tmm.senatedb.rawdata.resources.FormalPreferencesResource
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.io.ZipFileUtils.{ImprovedPath, ImprovedZipFile}
import au.id.tmm.utilities.option.OptionUtils.ImprovedOption

import scala.io.Source
import scala.util.Try

object LoadingFormalPreferences {

  def csvLinesOf(dataDir: Path, election: SenateElection, state: State): Try[Source] = {
    for {
      matchingResource <- resourceMatching(election, state)
      dataFilePath <- findRawDataWithIntegrityCheckFor(dataDir, matchingResource)
      inputStream <- csvInputStreamFrom(matchingResource, dataFilePath)
      source <- Try(Source.fromInputStream(inputStream, "UTF-8"))
    } yield source
  }

  private def resourceMatching(election: SenateElection, state: State): Try[FormalPreferencesResource] =
    FormalPreferencesResource.of(election, state)
      .failIfAbsent(new UnsupportedOperationException(s"Could not find raw data for $state at $election"))

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
