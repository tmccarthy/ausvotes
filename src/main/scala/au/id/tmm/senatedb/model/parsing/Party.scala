package au.id.tmm.senatedb.model.parsing

import au.id.tmm.senatedb.model.SenateElection

// TODO represent independents in a union type
final case class Party(election: SenateElection, name: String)
