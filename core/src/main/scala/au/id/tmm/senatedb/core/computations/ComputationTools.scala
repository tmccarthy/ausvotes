package au.id.tmm.senatedb.core.computations

import au.id.tmm.senatedb.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.core.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.senatedb.core.computations.howtovote.MatchingHowToVoteCalculator

final case class ComputationTools(normaliser: BallotNormaliser,
                                  firstPreferenceCalculator: FirstPreferenceCalculator,
                                  matchingHowToVoteCalculator: MatchingHowToVoteCalculator
                                 ) {
}
