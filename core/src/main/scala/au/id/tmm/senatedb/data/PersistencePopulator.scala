package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.database.Persistence
import au.id.tmm.senatedb.data.rawdatastore.RawDataStore

import scala.concurrent.ExecutionContext

class PersistencePopulator private (val persistence: Persistence,
                                    val rawDataStore: RawDataStore)
                                   (implicit val executionContext: ExecutionContext)
  extends PopulatesWithGroupsAndCandidates
    with PopulatesWithBallots{


}

object PersistencePopulator {
  def apply(persistence: Persistence, rawDataStore: RawDataStore)
           (implicit executionContext: ExecutionContext): PersistencePopulator =
    new PersistencePopulator(persistence, rawDataStore)
}