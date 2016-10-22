package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.computations.BallotWithFacts

trait ReportGenerator {

  type T_REPORT <: Report

  def generateFor(ballotsWithFacts: Vector[BallotWithFacts]): T_REPORT

}
