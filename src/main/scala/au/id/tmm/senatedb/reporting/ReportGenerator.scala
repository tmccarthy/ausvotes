package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.utilities.geo.australia.State

trait ReportGenerator {

  type T_REPORT <: Report

  def generateFor(state: State, ballotsWithFacts: Vector[BallotWithFacts]): T_REPORT

}
