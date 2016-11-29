package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.model.computation.SavingsProvision
import au.id.tmm.senatedb.tallies.Tallier.{NormalTallier, TieredTallier}
import au.id.tmm.utilities.geo.australia.State

object CountBallotSavingsProvisionUsage {
  object Nationally extends NormalTallier[SavingsProvision] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): Tally[SavingsProvision] = {
      val builder = Tally.Builder[SavingsProvision]()

      ballotsWithFacts.toStream
        .flatMap(_.savingsProvisionsUsed)
        .foreach(builder.increment)

      builder.build()
    }
  }

  object ByState extends TieredTallier[State, SavingsProvision] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): TieredTally[State, SavingsProvision] = {
      val builder = TieredTally.Builder[State, SavingsProvision]()

      for {
        ballotWithFacts <- ballotsWithFacts
        usedSavingsProvision <- ballotWithFacts.savingsProvisionsUsed
      } {
        builder.increment(ballotWithFacts.ballot.state, usedSavingsProvision)
      }

      builder.build()
    }
  }
}
