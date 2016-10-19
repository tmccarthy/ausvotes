package au.id.tmm.senatedb.computations

import au.id.tmm.senatedb.computations.donkeyvotes.DonkeyVoteDetector
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.senatedb.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.utilities.geo.australia.State

object BallotFactsComputation {

  def computeFactsFor(election: SenateElection,
                      state: State,
                      groupsAndCandidates: GroupsAndCandidates,
                      divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                      computationTools: ComputationTools,
                      ballots: Iterable[Ballot]): Iterable[BallotFacts] = {

    ballots.map(ballot => {
      BallotFacts(
        ballot = ballot,
        normalisedBallot = computationTools.normaliser.normalise(ballot),
        isDonkeyVote = DonkeyVoteDetector.isDonkeyVote(ballot)
      )
    })
  }

}
