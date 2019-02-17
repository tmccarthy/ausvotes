package au.id.tmm.ausvotes.core.computations

import au.id.tmm.ausvotes.core.model.computation.{BallotExhaustion, FirstPreference, SavingsProvision}
import au.id.tmm.ausvotes.model.federal.senate.{NormalisedSenateBallot, SenateBallot, SenateHtv}

final case class BallotWithFacts(
                                  ballot: SenateBallot,
                                  normalisedBallot: NormalisedSenateBallot,
                                  isDonkeyVote: Boolean,
                                  firstPreference: FirstPreference,
                                  matchingHowToVote: Option[SenateHtv],
                                  exhaustion: BallotExhaustion,
                                  savingsProvisionsUsed: Set[SavingsProvision],
                                )
