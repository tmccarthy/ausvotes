package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.engine.{ParsedDataStore, TallyEngine}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.tallies.{Tallier, Tallies}
import au.id.tmm.utilities.geo.australia.State

import scala.concurrent.{ExecutionContext, Future}

final class MockTallyEngine private (talliesToReturn: Tallies) extends TallyEngine {
  override def runFor(parsedDataStore: ParsedDataStore,
                      election: SenateElection,
                      states: Set[State],
                      talliers: Set[Tallier])(implicit ec: ExecutionContext): Future[Tallies] = Future(talliesToReturn)
}

object MockTallyEngine {
  def thatReturns(tallies: Tallies): MockTallyEngine = new MockTallyEngine(talliesToReturn = tallies)
}