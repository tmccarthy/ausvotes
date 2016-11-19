package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.computations.BallotWithFacts

trait Tallier {

  type TallyType <: TallyLike

  def tally(ballotsWithFacts: Vector[BallotWithFacts]): TallyType

  def isOfTallyType(tallyLike: TallyLike): Boolean

}

object Tallier {
  trait SimpleTallier extends Tallier {
    final override type TallyType = SimpleTally

    final override def isOfTallyType(tallyLike: TallyLike): Boolean = tallyLike.isInstanceOf[TallyType]
  }

  trait NormalTallier[A] extends Tallier {
    final override type TallyType = Tally[A]

    final override def isOfTallyType(tallyLike: TallyLike): Boolean = tallyLike.isInstanceOf[Tally[A]]
  }

  trait TieredTallier[A, B] extends Tallier {
    final override type TallyType = TieredTally[A, B]

    final override def isOfTallyType(tallyLike: TallyLike): Boolean = tallyLike.isInstanceOf[TieredTally[A, B]]
  }
}