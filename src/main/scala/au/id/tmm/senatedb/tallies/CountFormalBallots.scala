package au.id.tmm.senatedb.tallies
import au.id.tmm.senatedb.computations.BallotWithFacts

object CountFormalBallots {

  object Nationally extends Tallier {
    override type TallyType = SimpleTally

    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): SimpleTally = SimpleTally(ballotsWithFacts.size)

    override def isOfTallyType(tallyLike: TallyLike): Boolean = tallyLike.isInstanceOf[SimpleTally]
  }

}
