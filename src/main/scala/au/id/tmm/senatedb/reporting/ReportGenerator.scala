package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.computations.BallotFacts
import au.id.tmm.utilities.geo.australia.State

trait ReportGenerator[A <: Report[A]] {

  def generateFor(state: State, ballotsWithFacts: Vector[BallotFacts]): A

}
