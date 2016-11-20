package au.id.tmm.senatedb.core.computations

import au.id.tmm.senatedb.core.model.HowToVoteCard
import au.id.tmm.senatedb.core.model.computation.{BallotExhaustion, FirstPreference, NormalisedBallot}
import au.id.tmm.senatedb.core.model.parsing.Ballot

final case class BallotWithFacts(ballot: Ballot,
                                 normalisedBallot: NormalisedBallot,
                                 isDonkeyVote: Boolean,
                                 firstPreference: FirstPreference,
                                 matchingHowToVote: Option[HowToVoteCard],
                                 exhaustion: BallotExhaustion) {
}
