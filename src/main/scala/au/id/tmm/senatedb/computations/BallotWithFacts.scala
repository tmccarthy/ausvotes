package au.id.tmm.senatedb.computations

import au.id.tmm.senatedb.model.HowToVoteCard
import au.id.tmm.senatedb.model.computation.NormalisedBallot
import au.id.tmm.senatedb.model.parsing.{Ballot, Party}

final case class BallotWithFacts(ballot: Ballot,
                                 normalisedBallot: NormalisedBallot,
                                 isDonkeyVote: Boolean,
                                 firstPreferencedParty: Option[Party],
                                 matchingHowToVote: Option[HowToVoteCard]) {
}
