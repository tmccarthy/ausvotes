package au.id.tmm.senatedb.computations

import au.id.tmm.senatedb.model.HowToVoteCard
import au.id.tmm.senatedb.model.computation.{FirstPreference, NormalisedBallot}
import au.id.tmm.senatedb.model.parsing.Ballot

final case class BallotWithFacts(ballot: Ballot,
                                 normalisedBallot: NormalisedBallot,
                                 isDonkeyVote: Boolean,
                                 firstPreference: FirstPreference,
                                 matchingHowToVote: Option[HowToVoteCard]) {
}
