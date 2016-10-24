package au.id.tmm.senatedb.computations

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.senatedb.fixtures._
import au.id.tmm.senatedb.model.computation.NormalisedBallot
import au.id.tmm.senatedb.model.parsing.Party
import au.id.tmm.senatedb.model.{DivisionsAndPollingPlaces, SenateElection}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class BallotFactsComputationSpec extends ImprovedFlatSpec {
  private val election = SenateElection.`2016`
  private val state = State.ACT
  private val groupsAndCandidates = GroupsAndCandidates.ACT.groupsAndCandidates
  private val divisionsAndPollingPlaces = DivisionsAndPollingPlaces(Divisions.ACT.divisions, PollingPlaces.ACT.pollingPlaces)

  private val normaliser = BallotNormaliser(election, state, groupsAndCandidates.candidates)
  private val firstPreferenceCalculator = FirstPreferenceCalculator(election, state, groupsAndCandidates.candidates)
  private val computationTools = ComputationTools(normaliser, firstPreferenceCalculator)

  private val ballotMaker = BallotMaker(Candidates.ACT)

  private val testBallot = Ballots.ACT.formalAtl

  "ballot facts computation" should "correctly match the original ballot to its facts" in {
    val allBallotFacts = BallotFactsComputation.computeFactsFor(
      election,
      state,
      groupsAndCandidates,
      divisionsAndPollingPlaces,
      computationTools,
      Vector(testBallot)
    )

    val ballotWithFacts = allBallotFacts.head

    assert(ballotWithFacts.ballot eq testBallot)
  }

  it should "compute the normalised ballot" in {
    val allBallotFacts = BallotFactsComputation.computeFactsFor(
      election,
      state,
      groupsAndCandidates,
      divisionsAndPollingPlaces,
      computationTools,
      Vector(testBallot)
    )

    val ballotWithFacts = allBallotFacts.head

    val expectedNormalisedAtl =
      ballotMaker.candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")

    val expectedNormalisedBallot = NormalisedBallot(
      atlGroupOrder = ballotMaker.groupOrder("A", "B", "C", "D", "E", "F"),
      atlCandidateOrder = expectedNormalisedAtl,
      atlFormalPreferenceCount = 6,
      btlCandidateOrder = Vector.empty,
      btlFormalPreferenceCount = 0,
      canonicalOrder = expectedNormalisedAtl
    )

    assert(ballotWithFacts.normalisedBallot === expectedNormalisedBallot)
  }

  it should "compute whether the ballot is a donkey vote" in {
    val allBallotFacts = BallotFactsComputation.computeFactsFor(
      election,
      state,
      groupsAndCandidates,
      divisionsAndPollingPlaces,
      computationTools,
      Vector(testBallot)
    )

    val ballotWithFacts = allBallotFacts.head

    assert(ballotWithFacts.isDonkeyVote)
  }

  it should "compute the first preferenced party" in {
    val allBallotFacts = BallotFactsComputation.computeFactsFor(
      election,
      state,
      groupsAndCandidates,
      divisionsAndPollingPlaces,
      computationTools,
      Vector(testBallot)
    )

    val ballotWithFacts = allBallotFacts.head

    assert(ballotWithFacts.firstPreferencedParty === Some(Party(SenateElection.`2016`, "Liberal Democratic Party")))
  }
}
