package au.id.tmm.senatedb.webapp.services

import au.id.tmm.senatedb.core.engine.ParsedDataStore
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.webapp.persistence.daos.{DivisionDao, VoteCollectionPointDao}
import com.google.inject.Inject
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

final class DivisionAndVoteCollectionPointPopulationService @Inject()(divisionDao: DivisionDao,
                                                                      voteCollectionPointDao: VoteCollectionPointDao,
                                                                      parsedDataStore: ParsedDataStore) {

  def populateDivisionsAndVoteCollectionPointsFor(election: SenateElection): Future[Unit] = {
    Future(parsedDataStore.divisionsAndPollingPlacesFor(election)).flatMap { divisionsAndPollingPlaces =>

      val writePollingPlacesFuture = voteCollectionPointDao.write(divisionsAndPollingPlaces.pollingPlaces) // TODO absent etc?
      val writeDivisionsFuture = divisionDao.write(divisionsAndPollingPlaces.divisions)

      for {
        _ <- writePollingPlacesFuture
        _ <- writeDivisionsFuture
      } yield ()
    }
  }

}
