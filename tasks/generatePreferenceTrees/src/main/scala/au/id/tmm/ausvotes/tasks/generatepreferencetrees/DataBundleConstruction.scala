package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.model.parsing.{Ballot, CandidatePosition}
import au.id.tmm.ausvotes.core.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.countstv.model.preferences.PreferenceTree
import au.id.tmm.utilities.geo.australia.State
import scalaz.zio.IO

object DataBundleConstruction {

  def constructDataBundle(
                           election: SenateElection,
                           state: State,
                           groupsAndCandidates: GroupsAndCandidates,
                           divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                           ballots: Iterator[Ballot],
                         ): IO[Exception, DataBundleForElection] = {
    val relevantGroupsAndCandidates = groupsAndCandidates.findFor(election, state)
    val relevantDivisionsAndPollingPlaces = divisionsAndPollingPlaces.findFor(election, state)

    val candidates = relevantGroupsAndCandidates.candidates

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
          preferenceTree,
        )
      }
  }

  final case class DataBundleForElection(
                                          election: SenateElection,
                                          state: State,
                                          groupsAndCandidates: GroupsAndCandidates,
                                          divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                          preferenceTree: PreferenceTree.RootPreferenceTree[CandidatePosition],
                                        )

}
