package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.computations.BallotWithFacts
import au.id.tmm.senatedb.core.model.parsing.{BallotGroup, Division, Party, VoteCollectionPoint}
import au.id.tmm.senatedb.core.tallies.Tally.MapOps
import au.id.tmm.utilities.geo.australia.State

trait PerBallotTallier {

  def countFor(ballotWithFacts: BallotWithFacts): Double

  private def countFor(ballotsWithFacts: Iterable[BallotWithFacts]): Double = {
    ballotsWithFacts.map(countFor).foldLeft(0d)(_ + _)
  }

  object Nationally extends Tallier.SimpleTallier {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): SimpleTally = {
      SimpleTally(countFor(ballotsWithFacts))
    }
  }

  object NationallyByFirstPreference extends Tallier.NormalTallier[Party] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): Tally[Party] =
      ballotsWithFacts.toStream
        .groupBy(_.firstPreference.party.nationalEquivalent)
        .mapValues(countFor)
        .toTally
  }

  object ByState extends Tallier.NormalTallier[State] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): Tally[State] =
      ballotsWithFacts.toStream
        .groupBy(_.ballot.state)
        .mapValues(countFor)
        .toTally
  }

  object ByDivision extends Tallier.NormalTallier[Division] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): Tally[Division] =
      ballotsWithFacts.toStream
        .groupBy(_.ballot.division)
        .mapValues(countFor)
        .toTally
  }

  object ByVoteCollectionPoint extends Tallier.NormalTallier[VoteCollectionPoint] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): Tally[VoteCollectionPoint] =
      ballotsWithFacts.toStream
        .groupBy(_.ballot.voteCollectionPoint)
        .mapValues(countFor)
        .toTally
  }

  object ByFirstPreferencedGroup extends Tallier.TieredTallier[State, BallotGroup] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): TieredTally[State, BallotGroup] = {
      val tallyBuilder = TieredTally.Builder[State, BallotGroup]()

      ballotsWithFacts.toStream
        .foreach(ballotWithFacts => {
          tallyBuilder.incrementBy(
            ballotWithFacts.ballot.state,
            ballotWithFacts.firstPreference.group,
            countFor(ballotWithFacts)
          )
        })

      tallyBuilder.build()
    }
  }
}
