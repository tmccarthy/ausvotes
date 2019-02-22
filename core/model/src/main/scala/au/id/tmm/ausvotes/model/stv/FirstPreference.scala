package au.id.tmm.ausvotes.model.stv

import au.id.tmm.ausvotes.model.Party

// TODO generalise and move to the model subproject
final case class FirstPreference[E](group: BallotGroup[E], party: Option[Party])
