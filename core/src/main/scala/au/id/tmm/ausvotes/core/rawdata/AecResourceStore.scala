package au.id.tmm.ausvotes.core.rawdata

import java.nio.file.{Files, NotDirectoryException, Path}

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.rawdata.download._
import au.id.tmm.ausvotes.core.rawdata.resources.{Resource, ResourceWithDigest}
import au.id.tmm.utilities.geo.australia.State

import scala.collection.concurrent.TrieMap
import scala.io.Source
import scala.util.Try

trait AecResourceStore {
  def distributionOfPreferencesFor(election: SenateElection, state: State): Try[Source]
  def firstPreferencesFor(election: SenateElection): Try[Source]
  def formalPreferencesFor(election: SenateElection, state: State): Try[Source]
  def pollingPlacesFor(election: SenateElection): Try[Source]
}

private [rawdata] final class LocalAecResourceStore(val location: Path) extends AecResourceStore {

  private val downloadMutexes: scala.collection.concurrent.Map[Resource, Object] = TrieMap()

  private def resourcePathFor(resource: Resource): Try[Path] = {
    downloadMutexes.getOrElseUpdate(resource, new Object).synchronized {
      resource match {
        case r: ResourceWithDigest => StorageUtils.findRawDataFor(r, location, performDigestCheck = true)
        case r: Resource => StorageUtils.findRawDataFor(r, location, performDigestCheck = false)
      }
    }
  }

  override def distributionOfPreferencesFor(election: SenateElection, state: State): Try[Source] =
    for {
      resource <- LoadingDistributionsOfPreferences.resourceMatching(election)
      resourcePath <- resourcePathFor(resource)
      source <- LoadingDistributionsOfPreferences.csvLinesFor(resource, resourcePath, state)
    } yield source

  override def firstPreferencesFor(election: SenateElection): Try[Source] =
    for {
      resource <- LoadingFirstPreferences.resourceMatching(election)
      resourcePath <- resourcePathFor(resource)
      source <- LoadingFirstPreferences.csvLinesOf(resourcePath)
    } yield source

  override def formalPreferencesFor(election: SenateElection, state: State): Try[Source] =
    for {
      resource <- LoadingFormalPreferences.resourceMatching(election, state)
      resourcePath <- resourcePathFor(resource)
      source <- LoadingFormalPreferences.csvLinesOf(resource, resourcePath)
    } yield source

  override def pollingPlacesFor(election: SenateElection): Try[Source] =
    for {
      resource <- LoadingPollingPlaces.resourceMatching(election)
      resourcePath <- resourcePathFor(resource)
      source <- LoadingPollingPlaces.csvLinesOf(resourcePath)
    } yield source

}

object AecResourceStore {

  @scala.annotation.tailrec
  def at(location: Path): AecResourceStore = {
    if (!location.isAbsolute) {
      at(location.toAbsolutePath)

    } else if (Files.isDirectory(location)) {
      new LocalAecResourceStore(location)

    } else if (Files.exists(location)) {
      throw new NotDirectoryException(location.toString)

    } else {
      new LocalAecResourceStore(Files.createDirectories(location))

    }
  }
}