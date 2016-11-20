package au.id.tmm.senatedb.core.fixtures

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.SenateElection.`2016`
import au.id.tmm.senatedb.core.rawdata.AecResourceStore
import au.id.tmm.utilities.geo.australia.State

import scala.io.Source
import scala.util.Try

object MockAecResourceStore extends AecResourceStore {
  override def distributionOfPreferencesFor(election: SenateElection, state: State): Try[Source] = {
    election match {
      case `2016` => sourceFromResource(s"SenateStateDOPDownload-20499-${state.abbreviation}.csv")
      case _ => badElection(election)
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
    Source.fromURL(getClass.getResource(resourceName))
  }
}
