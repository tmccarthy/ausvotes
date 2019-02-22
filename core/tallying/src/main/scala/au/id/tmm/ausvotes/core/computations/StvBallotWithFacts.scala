package au.id.tmm.ausvotes.core.computations

import au.id.tmm.ausvotes.core.model.computation.{BallotExhaustion, SavingsProvision}
import au.id.tmm.ausvotes.model.HowToVoteCard
import au.id.tmm.ausvotes.model.stv.{Ballot, FirstPreference, Group, NormalisedBallot}

// TODO find some way to be generic only around the election
final case class StvBallotWithFacts[E, J, I](
                                              ballot: Ballot[E, J, I],
                                              normalisedBallot: NormalisedBallot[E],
                                              isDonkeyVote: Boolean,
                                              firstPreference: FirstPreference[E],
                                              matchingHowToVote: Option[HowToVoteCard[E, Group[E]]],
                                              exhaustion: BallotExhaustion,
                                              savingsProvisionsUsed: Set[SavingsProvision],
                                            )
