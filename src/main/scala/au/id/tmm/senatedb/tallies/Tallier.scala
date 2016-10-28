package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.computations.BallotWithFacts

trait Tallier {

  type TallyType <: TallyLike

  def tally(ballotsWithFacts: Vector[BallotWithFacts]): TallyType

  def isOfTallyType(tallyLike: TallyLike): Boolean
}
