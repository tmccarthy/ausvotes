package au.id.tmm.senatedb.parsing.countdata

import au.id.tmm.senatedb.fixtures.Candidates
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.{Candidate, Independent, Name}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DistributionSourceCalculatorSpec extends ImprovedFlatSpec {

  import au.id.tmm.senatedb.fixtures.Ballots.ACT.ballotMaker.candidatePosition

  "a distribution source calculator" should "fail if more than one candidates share a short name" in {

    val candidates = Candidates.ACT.candidates +
      Candidate(SenateElection.`2016`, State.ACT, "42", Name("Jane", "Doe"), Independent, candidatePosition("UG2")) +
      Candidate(SenateElection.`2016`, State.ACT, "43", Name("John", "Doe"), Independent, candidatePosition("UG3"))

    intercept[IllegalStateException](new DistributionSourceCalculator(candidates))
  }

}
