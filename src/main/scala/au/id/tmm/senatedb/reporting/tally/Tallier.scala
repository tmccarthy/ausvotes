package au.id.tmm.senatedb.reporting.tally

import au.id.tmm.senatedb.computations.BallotWithFacts

trait Tallier {

  type TallyType <: TallyLike

  def generateFor(ballotsWithFacts: Vector[BallotWithFacts]): TallyType

}
