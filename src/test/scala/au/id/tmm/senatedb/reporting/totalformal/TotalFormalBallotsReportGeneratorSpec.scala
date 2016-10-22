package au.id.tmm.senatedb.reporting.totalformal

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.fixtures.{BallotMaker, Candidates}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.computation.NormalisedBallot
import au.id.tmm.senatedb.model.parsing.Party
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class TotalFormalBallotsReportGeneratorSpec extends ImprovedFlatSpec {

  private val ballotMaker = BallotMaker(Candidates.ACT)

  "a TotalFormalBallotsReportGenerator" should "compute the total number of ballots" in {

    val expectedNormalisedAtl =
      ballotMaker.candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")

    val ballotFacts = Vector(
      BallotWithFacts(
        ballotMaker.makeBallot(
          ballotMaker.orderedAtlPreferences("A", "B", "C", "D", "E", "F"),
          Map.empty
        ),
        NormalisedBallot(
          atlCandidateOrder = expectedNormalisedAtl,
          atlFormalPreferenceCount = 6,
          btlCandidateOrder = Vector.empty,
          btlFormalPreferenceCount = 0,
          canonicalOrder = expectedNormalisedAtl
        ),
        isDonkeyVote = true,
        firstPreferencedParty = Some(Party(SenateElection.`2016`, "Liberal Democratic Party"))
      )
    )

    val totalsReport = TotalFormalBallotsReportGenerator.generateFor(State.ACT, ballotFacts)

    assert(totalsReport.total === 1)
  }

}
