package au.id.tmm.senatedb.computations

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.computations.firstpreference.FirstPreferenceCalculator

final case class ComputationTools(normaliser: BallotNormaliser, firstPreferenceCalculator: FirstPreferenceCalculator) {
}
