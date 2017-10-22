package au.id.tmm.ausvotes.core.fixtures

import java.io.BufferedInputStream
import java.util.zip.GZIPInputStream

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.SenateElection.`2016`
import au.id.tmm.ausvotes.core.rawdata.AecResourceStore
import au.id.tmm.utilities.geo.australia.State
import org.apache.commons.io.FilenameUtils

import scala.io.Source
import scala.util.Try

object MockAecResourceStore extends AecResourceStore {
  override def distributionOfPreferencesFor(election: SenateElection, state: State): Try[Source] = {
    if (election == `2016`) {
      state match {
        case State.WA => sourceFromResource(s"SenateStateDOPDownload-20499-${state.abbreviation}.csv.gz")
        case _ => sourceFromResource(s"SenateStateDOPDownload-20499-${state.abbreviation}.csv")
      }
    } else {
      badElection(election)
    }
  }

  override def firstPreferencesFor(election: SenateElection): Try[Source] = {
    election match {
      case `2016` => sourceFromResource("firstPreferencesTest.csv")
      case _ => badElection(election)
    }
  }

  override def formalPreferencesFor(election: SenateElection, state: State): Try[Source] = {
    election match {
      case `2016` => sourceFromResource("formalPreferencesTest.csv")
      case _ => badElection(election)
    }
  }

  override def pollingPlacesFor(election: SenateElection): Try[Source] = {
    election match {
      case `2016` => sourceFromResource("GeneralPollingPlacesDownload-20499.csv")
    }
  }

  private def badElection(election: SenateElection) = throw new IllegalArgumentException(s"No data for $election")

  private def sourceFromResource(resourceName: String) = Try {
    if (FilenameUtils.getExtension(resourceName) == "gz") {
      Source.fromInputStream(new GZIPInputStream(new BufferedInputStream(getClass.getResourceAsStream(resourceName))))
    } else {
      Source.fromURL(getClass.getResource(resourceName))
    }
  }
}
