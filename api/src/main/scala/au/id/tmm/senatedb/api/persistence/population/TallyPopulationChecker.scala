package au.id.tmm.senatedb.api.persistence.population

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.tallies.Tallier
import au.id.tmm.utilities.concurrent.FutureCollectionUtils.FutureSetOps
import com.google.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TallyPopulationChecker @Inject() ()
                                       (implicit ec: ExecutionContext) {

  def unpopulatedOf(election: SenateElection, talliers: Set[Tallier]): Future[Set[Tallier]] = {
    talliers.filterEventually(resultIsPopulated(election, _).map(!_))
  }

  private def resultIsPopulated(election: SenateElection, tallier: Tallier): Future[Boolean] = ???
}
