package au.id.tmm.ausvotes.lambdas

import au.id.tmm.ausvotes.model.federal.senate.SenateCandidate

package object recount {

  type RequiresCandidates[A] = Set[SenateCandidate] => A

}
