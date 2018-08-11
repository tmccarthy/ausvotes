package au.id.tmm.ausvotes.lambdas

import au.id.tmm.ausvotes.core.model.parsing.Candidate

package object recount {

  type RequiresCandidates[A] = Set[Candidate] => A

}
