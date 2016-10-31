package au.id.tmm.senatedb.engine

import au.id.tmm.senatedb.fixtures.MockParsedDataStore
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.tallies.{CountFormalBallots, SimpleTally}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TallyEngineSpec extends ImprovedFlatSpec {

  private val parsedDataStore = MockParsedDataStore

  "the tally engine" can "perform the provided tallies" in {
    val resultFuture = TallyEngine.runFor(
      parsedDataStore,
      SenateElection.`2016`,
      Set(State.ACT),
      Set(CountFormalBallots.Nationally)
    )(scala.concurrent.ExecutionContext.global)

    val tallies = Await.result(resultFuture, Duration.Inf)

    assert(tallies.tallyBy(CountFormalBallots.Nationally) === SimpleTally(4))
  }
}
