package au.id.tmm.senatedb.computations

import au.id.tmm.senatedb.model.HowToVoteCard
import au.id.tmm.senatedb.model.computation.{BallotExhaustion, FirstPreference, NormalisedBallot, SavingsProvision}
import au.id.tmm.senatedb.model.parsing.Ballot

final case class BallotWithFacts(ballot: Ballot,
                                 normalisedBallot: NormalisedBallot,
                                 isDonkeyVote: Boolean,
                                 firstPreference: FirstPreference,
                                 matchingHowToVote: Option[HowToVoteCard],
                                 exhaustion: BallotExhaustion,
                                 savingsProvisionsUsed: Set[SavingsProvision]
                                ) {
}
