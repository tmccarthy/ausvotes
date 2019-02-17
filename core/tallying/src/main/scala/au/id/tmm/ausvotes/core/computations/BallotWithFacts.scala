package au.id.tmm.ausvotes.core.computations

import au.id.tmm.ausvotes.core.model.computation.{BallotExhaustion, FirstPreference, NormalisedBallot, SavingsProvision}
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallot, SenateHtv}

final case class BallotWithFacts(
                                  ballot: SenateBallot,
                                  normalisedBallot: NormalisedBallot,
                                  isDonkeyVote: Boolean,
                                  firstPreference: FirstPreference,
                                  matchingHowToVote: Option[SenateHtv],
                                  exhaustion: BallotExhaustion,
                                  savingsProvisionsUsed: Set[SavingsProvision],
                                )
