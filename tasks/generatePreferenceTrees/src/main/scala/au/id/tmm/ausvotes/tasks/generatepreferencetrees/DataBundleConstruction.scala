package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.model.parsing.{Ballot, Candidate, CandidatePosition}
import au.id.tmm.ausvotes.core.model.{CountData, DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.recountresources.CountSummary
import au.id.tmm.countstv.model.preferences.PreferenceTree
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import scalaz.zio.IO

object DataBundleConstruction {

  def constructDataBundle(
                           election: SenateElection,
                           state: State,
                           groupsAndCandidates: GroupsAndCandidates,
                           divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                           countData: CountData,
                           ballots: Iterator[Ballot],
                         ): IO[Exception, DataBundleForElection] = {
    val relevantGroupsAndCandidates = groupsAndCandidates.findFor(election, state)
    val relevantDivisionsAndPollingPlaces = divisionsAndPollingPlaces.findFor(election, state)

    val candidates = relevantGroupsAndCandidates.candidates

    val lookupCandidateByPosition: Map[CandidatePosition, Candidate] = candidates.map { candidate =>
      candidate.btlPosition -> candidate
    }.toMap

    val ineligibleCandidates = countData.ineligibleCandidates

    val candidateStatuses = countData.completedCount.outcomes

    val canonicalRecountResult = CountSummary(
      request = CountSummary.Request(
        election,
        state,
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

    val ballotNormaliser = BallotNormaliser(election, state, candidates)

    val numPapersHint = StateUtils.numBallots(state)

    val preparedBallots = ballots.map(ballotNormaliser.normalise(_).canonicalOrder.map(lookupCandidateByPosition))

    IO.syncException(PreferenceTree.fromIterator(candidates, numPapersHint)(preparedBallots))
      .map { preferenceTree =>
        DataBundleForElection(
          election,
          state,
          relevantGroupsAndCandidates,
          relevantDivisionsAndPollingPlaces,
          canonicalRecountResult,
          preferenceTree,
        )
      }
  }

  final case class DataBundleForElection(
                                          election: SenateElection,
                                          state: State,
                                          groupsAndCandidates: GroupsAndCandidates,
                                          divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                          canonicalCountResult: CountSummary,
                                          preferenceTree: PreferenceTree.RootPreferenceTree[Candidate],
                                        )

}
