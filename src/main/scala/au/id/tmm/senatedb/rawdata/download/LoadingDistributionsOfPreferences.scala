package au.id.tmm.senatedb.rawdata.download

import java.io.InputStream
import java.nio.file.Path
import java.util.zip.{ZipEntry, ZipFile}

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.download.StorageUtils.findRawDataWithIntegrityCheckFor
import au.id.tmm.senatedb.rawdata.resources.DistributionOfPreferencesResource
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.io.ZipFileUtils.{ImprovedPath, ImprovedZipFile}
import au.id.tmm.utilities.option.OptionUtils.ImprovedOption

import scala.io.Source
import scala.util.Try

object LoadingDistributionsOfPreferences {

  def csvLinesOf(dataDir: Path, election: SenateElection, state: State): Try[Source] = {
    for {
      matchingResource <- resourceMatching(election)
      dataFilePath <- findRawDataWithIntegrityCheckFor(dataDir, matchingResource)
      inputStream <- csvInputStreamFrom(matchingResource, dataFilePath, state)
      source <- Try(Source.fromInputStream(inputStream, "UTF-8"))
    } yield source
  }

  private def resourceMatching(election: SenateElection): Try[DistributionOfPreferencesResource] =
    DistributionOfPreferencesResource.of(election)
      .failIfAbsent(new UnsupportedOperationException(s"Could not find a distribution of preferences for $election"))

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
