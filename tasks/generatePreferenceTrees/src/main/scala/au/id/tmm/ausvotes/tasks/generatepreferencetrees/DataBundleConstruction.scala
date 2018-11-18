package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.model.parsing.{Ballot, Candidate, CandidatePosition}
import au.id.tmm.ausvotes.core.model.{CountData, DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.recountresources.RecountResult
import au.id.tmm.countstv.model.CandidateStatuses
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

    val ineligibleCandidates = countData.ineligibleCandidates.flatMap(lookupCandidateByPosition.get)

    val canonicalRecountResult = RecountResult(
      election,
      state,
      numVacancies = countData.completedCount.numVacancies,
      ineligibleCandidates = ineligibleCandidates,
      candidateOutcomeProbabilities = ProbabilityMeasure.Always(
        CandidateStatuses(
          countData.completedCount.outcomes.asMap.map { case (candidatePosition, candidateOutcome) =>
            lookupCandidateByPosition(candidatePosition) -> candidateOutcome
          }
        )
      )
    )

    val ballotNormaliser = BallotNormaliser(election, state, candidates)

    val numPapersHint = StateUtils.numBallots(state)

    val preparedBallots = ballots.map(ballotNormaliser.normalise(_).canonicalOrder)

    IO.syncException(PreferenceTree.fromIterator(candidates.map(_.btlPosition), numPapersHint)(preparedBallots))
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
                                          canonicalCountResult: RecountResult,
                                          preferenceTree: PreferenceTree.RootPreferenceTree[CandidatePosition],
                                        )

}
