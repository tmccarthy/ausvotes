package au.id.tmm.senatedb.model.computation

import au.id.tmm.senatedb.model.parsing.{BallotGroup, Party}

final case class FirstPreference(group: BallotGroup, party: Option[Party]) {

}
