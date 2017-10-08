package au.id.tmm.ausvotes.core.computations

import au.id.tmm.ausvotes.core.model.HowToVoteCard
import au.id.tmm.ausvotes.core.model.computation.{BallotExhaustion, FirstPreference, NormalisedBallot, SavingsProvision}
import au.id.tmm.ausvotes.core.model.parsing.Ballot

final case class BallotWithFacts(ballot: Ballot,
                                 normalisedBallot: NormalisedBallot,
                                 isDonkeyVote: Boolean,
                                 firstPreference: FirstPreference,
                                 matchingHowToVote: Option[HowToVoteCard],
                                 exhaustion: BallotExhaustion,
                                 savingsProvisionsUsed: Set[SavingsProvision]
                                ) {
}
