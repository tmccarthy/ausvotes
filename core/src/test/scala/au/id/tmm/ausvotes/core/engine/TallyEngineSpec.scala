package au.id.tmm.ausvotes.core.engine

import au.id.tmm.ausvotes.core.fixtures.MockParsedDataStore
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.tallies.{BallotCounter, TallierBuilder, Tally0}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TallyEngineSpec extends ImprovedFlatSpec {

  private val parsedDataStore = MockParsedDataStore

  "the tally engine" can "perform the provided tallies" in {
    val tallier = TallierBuilder.counting(BallotCounter.FormalBallots).overall()

    val resultFuture = TallyEngine.runFor(
      parsedDataStore,
      SenateElection.`2016`,
      Set(State.ACT),
      Set(tallier)
    )(scala.concurrent.ExecutionContext.global)

    val tallies = Await.result(resultFuture, Duration.Inf)

    assert(tallies.tallyProducedBy(tallier) === Tally0(4))
  }
}
