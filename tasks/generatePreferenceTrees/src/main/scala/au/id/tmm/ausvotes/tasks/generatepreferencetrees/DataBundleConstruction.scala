package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.model.{DivisionsAndPollingPlaces, GroupsAndCandidates}
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.shared.recountresources.CountSummary
import au.id.tmm.countstv.model.preferences.PreferenceTree
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import scalaz.zio.IO

object DataBundleConstruction {

  def constructDataBundle(
                           election: SenateElectionForState,
                           groupsAndCandidates: GroupsAndCandidates,
                           divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                           countData: SenateCountData,
                           ballots: Iterator[SenateBallot],
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

    val ballotNormaliser = BallotNormaliser(election, candidates)

    val numPapersHint = StateUtils.numBallots(election.state)

    val preparedBallots = ballots.map(ballotNormaliser.normalise(_).canonicalOrder)

    IO.syncException(PreferenceTree.fromIterator(candidates, numPapersHint)(preparedBallots))
      .map { preferenceTree =>
        DataBundleForElection(
          election,
          relevantGroupsAndCandidates,
          relevantDivisionsAndPollingPlaces,
          canonicalRecountResult,
          preferenceTree,
        )
      }
  }

  final case class DataBundleForElection(
                                          election: SenateElectionForState,
                                          groupsAndCandidates: GroupsAndCandidates,
                                          divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                          canonicalCountResult: CountSummary,
                                          preferenceTree: PreferenceTree.RootPreferenceTree[SenateCandidate],
                                        )

}
