package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.database.Persistence
import au.id.tmm.senatedb.data.rawdatastore.RawDataStore
import slick.driver.SQLiteDriver

import scala.collection.generic.CanBuildFrom
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class PersistencePopulator private (val persistence: Persistence,
                                    val rawDataStore: RawDataStore)
                                   (implicit val executionContext: ExecutionContext)
  extends PopulatesWithGroupsAndCandidates
    with PopulatesWithBallots
    with PopulatesWithCountData {

  // This monstrous signature is just copied from Future.sequence
  protected def sequenceWritingFutures[A, M[X] <: TraversableOnce[X]](in: => M[Future[A]])(implicit cbf: CanBuildFrom[M[Future[A]], A, M[A]]): Future[M[A]] = {
    if (this.persistence.dal.driver == SQLiteDriver) {
      Future {
        val resultBuilder = cbf.apply(in)

        in.foreach(future => resultBuilder.+=(Await.result(future, Duration.Inf)))

        resultBuilder.result()
      }
    } else {
      Future.sequence(in)
    }
  }
}

object PersistencePopulator {
  def apply(persistence: Persistence, rawDataStore: RawDataStore)
           (implicit executionContext: ExecutionContext): PersistencePopulator =
    new PersistencePopulator(persistence, rawDataStore)
}