package au.id.tmm.ausvotes.core.model.computation

import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.senate.SenateBallotGroup

// TODO generalise and move to the model subproject
final case class FirstPreference(group: SenateBallotGroup, party: Option[Party])
