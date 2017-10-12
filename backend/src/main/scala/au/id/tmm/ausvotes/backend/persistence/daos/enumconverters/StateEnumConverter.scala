package au.id.tmm.ausvotes.backend.persistence.daos.enumconverters

import au.id.tmm.utilities.geo.australia.State

private[daos] object StateEnumConverter extends EnumConverter[State] {
  override def apply(enumVal: State): String = enumVal.abbreviation

  override def apply(stringVal: String): State = State.fromAbbreviation(stringVal).get
}
