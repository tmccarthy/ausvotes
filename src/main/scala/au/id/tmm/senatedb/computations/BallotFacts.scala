package au.id.tmm.senatedb.computations

import au.id.tmm.senatedb.model.computation.NormalisedBallot
import au.id.tmm.senatedb.model.parsing.Ballot

final case class BallotFacts(ballot: Ballot,
                             normalisedBallot: NormalisedBallot,
                             isDonkeyVote: Boolean) {
}
