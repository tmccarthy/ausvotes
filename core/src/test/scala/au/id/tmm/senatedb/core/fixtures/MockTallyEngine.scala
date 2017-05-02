package au.id.tmm.senatedb.core.fixtures

import au.id.tmm.senatedb.core.engine.{ParsedDataStore, TallyEngine}
import au.id.tmm.senatedb.core.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.senatedb.core.tallies.{Tallier, Tallies}
import au.id.tmm.utilities.geo.australia.State

import scala.concurrent.{ExecutionContext, Future}

final class MockTallyEngine private (talliesToReturn: Tallies) extends TallyEngine {
  override def runFor(parsedDataStore: ParsedDataStore,
                      election: SenateElection,
                      states: Set[State],
                      talliers: Set[Tallier])(implicit ec: ExecutionContext): Future[Tallies] = Future(talliesToReturn)

  override def runFor(parsedDataStore: ParsedDataStore,
                      election: SenateElection,
                      states: Set[State],
                      divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                      groupsAndCandidates: GroupsAndCandidates,
                      talliers: Set[Tallier]
                               )(implicit ec: ExecutionContext): Future[Tallies] = Future(talliesToReturn)
}

object MockTallyEngine {
  def thatReturns(tallies: Tallies): MockTallyEngine = new MockTallyEngine(talliesToReturn = tallies)
}