package au.id.tmm.senatedb.model.parsing

import au.id.tmm.senatedb.model.SenateElection

final case class Party(election: SenateElection, name: String)
