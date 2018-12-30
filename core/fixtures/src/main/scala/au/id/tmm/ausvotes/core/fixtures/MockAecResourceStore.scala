package au.id.tmm.ausvotes.core.fixtures

import java.io.BufferedInputStream
import java.util.zip.GZIPInputStream

import au.id.tmm.ausvotes.core.rawdata.AecResourceStore
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State
import org.apache.commons.io.FilenameUtils

import scala.io.Source
import scala.util.{Failure, Try}

// TODO move to the core tests
object MockAecResourceStore extends AecResourceStore {
  override def distributionOfPreferencesFor(election: SenateElectionForState): Try[Source] = {
    election match {
      case SenateElectionForState(SenateElection.`2016`, state @ State.WA) => sourceFromResource(s"SenateStateDOPDownload-20499-${state.abbreviation}.csv.gz")
      case SenateElectionForState(SenateElection.`2016`, state) => sourceFromResource(s"SenateStateDOPDownload-20499-${state.abbreviation}.csv")
      case _ => badElection(election)
    }
  }

  override def firstPreferencesFor(election: SenateElection): Try[Source] = {
    election match {
      case SenateElection.`2016` => sourceFromResource("firstPreferencesTest.csv")
      case _ => badElection(election)
    }
  }

  override def formalPreferencesFor(election: SenateElectionForState): Try[Source] = {
    election match {
      case SenateElectionForState(SenateElection.`2016`, _) => sourceFromResource("formalPreferencesTest.csv")
      case _ => badElection(election)
    }
  }

  override def pollingPlacesFor(election: FederalElection): Try[Source] = {
    election match {
      case FederalElection.`2016` => sourceFromResource("GeneralPollingPlacesDownload-20499.csv")
      case e => Failure(new NotImplementedError(s"No support for election $e"))
    }
  }

  private def badElection(election: Any) = throw new IllegalArgumentException(s"No data for $election")

  private def sourceFromResource(resourceName: String) = Try {
    if (FilenameUtils.getExtension(resourceName) == "gz") {
      Source.fromInputStream(new GZIPInputStream(new BufferedInputStream(getClass.getResourceAsStream(resourceName))))
    } else {
      Source.fromURL(getClass.getResource(resourceName))
    }
  }
}
