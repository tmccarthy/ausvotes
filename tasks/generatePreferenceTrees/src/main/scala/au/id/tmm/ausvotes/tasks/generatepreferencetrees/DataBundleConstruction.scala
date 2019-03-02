package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.data_sources.aec.federal.extras.CountRules
import au.id.tmm.ausvotes.data_sources.common.Fs2Interop._
import au.id.tmm.ausvotes.model.federal.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import au.id.tmm.ausvotes.shared.recountresources.CountSummary
import au.id.tmm.countstv.model.preferences.PreferenceTree
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import fs2.Stream
import scalaz.zio.IO

object DataBundleConstruction {

  def constructDataBundle(
                           election: SenateElectionForState,
                           groupsAndCandidates: SenateGroupsAndCandidates,
                           divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                           countData: SenateCountData,
                           ballots: Stream[IO[Throwable, +?], SenateBallot],
                         ): IO[Exception, DataBundleForElection] = {
    val relevantGroupsAndCandidates = groupsAndCandidates.findFor(election)
    val relevantDivisionsAndPollingPlaces = divisionsAndPollingPlaces.findFor(election.election.federalElection, election.state)

    val candidates = relevantGroupsAndCandidates.candidates

    val ineligibleCandidates = countData.completedCount.outcomes.ineligibleCandidates

    val candidateStatuses = countData.completedCount.outcomes

    val canonicalRecountResult = CountSummary(
      request = CountSummary.Request(
        election,
        numVacancies = countData.completedCount.countParams.numVacancies,
        ineligibleCandidates = ineligibleCandidates,
        doRounding = true,
      ),
      outcomePossibilities = ProbabilityMeasure.Always(
        CountSummary.Outcome(
          elected = candidateStatuses.electedCandidates,
          exhaustedVotes = countData.completedCount.countSteps.last.candidateVoteCounts.exhausted,
          roundingError = countData.completedCount.countSteps.last.candidateVoteCounts.roundingError,
          candidateOutcomes = candidateStatuses,
        )
      )
    )

    val ballotNormaliser = BallotNormaliser(CountRules.normalisationRulesFor(election.election), election, candidates)

    val numPapersHint = StateUtils.numBallots(election.state)

    val preparedBallots = ballots.map(ballotNormaliser.normalise(_).canonicalOrder.getOrElse(Vector.empty))

    preparedBallots.compile.toChunk.map { ballotsChunk =>
      DataBundleForElection(
        election,
        relevantGroupsAndCandidates,
        relevantDivisionsAndPollingPlaces,
        canonicalRecountResult,
        PreferenceTree.fromIterator(candidates, numPapersHint)(ballotsChunk.iterator),
      )
    }.swallowThrowables
  }

  final case class DataBundleForElection(
                                          election: SenateElectionForState,
                                          groupsAndCandidates: SenateGroupsAndCandidates,
                                          divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                          canonicalCountResult: CountSummary,
                                          preferenceTree: PreferenceTree.RootPreferenceTree[SenateCandidate],
                                        )

}
