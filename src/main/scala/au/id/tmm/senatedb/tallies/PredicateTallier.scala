package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.model.parsing.{BallotGroup, Division, Party, VoteCollectionPoint}
import au.id.tmm.senatedb.tallies.Tally.MapOps
import au.id.tmm.utilities.geo.australia.State

trait PredicateTallier {

  def shouldCount(ballotWithFacts: BallotWithFacts): Boolean

  object Nationally extends Tallier.SimpleTallier {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): SimpleTally =
      SimpleTally(ballotsWithFacts.count(shouldCount))
  }

  object NationallyByFirstPreference extends Tallier.NormalTallier[Party] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): Tally[Party] =
      ballotsWithFacts.toStream
        .filter(shouldCount)
        .groupBy(_.firstPreference.party.nationalEquivalent)
        .mapValues(_.size.toDouble)
        .toTally
  }

  object ByState extends Tallier.NormalTallier[State] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): Tally[State] =
      ballotsWithFacts.toStream
        .filter(shouldCount)
        .groupBy(_.ballot.state)
        .mapValues(_.size.toDouble)
        .toTally
  }

  object ByDivision extends Tallier.NormalTallier[Division] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): Tally[Division] =
      ballotsWithFacts.toStream
        .filter(shouldCount)
        .groupBy(_.ballot.division)
        .mapValues(_.size.toDouble)
        .toTally
  }

  object ByVoteCollectionPoint extends Tallier.NormalTallier[VoteCollectionPoint] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): Tally[VoteCollectionPoint] =
      ballotsWithFacts.toStream
        .filter(shouldCount)
        .groupBy(_.ballot.voteCollectionPoint)
        .mapValues(_.size.toDouble)
        .toTally
  }

  object ByFirstPreferencedGroup extends Tallier.TieredTallier[State, BallotGroup] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): TieredTally[State, BallotGroup] = {
      val tallyBuilder = TieredTally.Builder[State, BallotGroup]()

      ballotsWithFacts.toStream
        .filter(shouldCount)
        .foreach(ballotWithFacts => {
          tallyBuilder.increment(ballotWithFacts.ballot.state, ballotWithFacts.firstPreference.group)
        })

      tallyBuilder.build()
    }
  }
}
