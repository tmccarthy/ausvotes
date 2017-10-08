package au.id.tmm.ausvotes.api.persistence.daos.enumconverters

import au.id.tmm.ausvotes.api.persistence.daos.ElectionDao
import au.id.tmm.ausvotes.core.model.SenateElection

private[daos] object ElectionEnumConverter extends EnumConverter[SenateElection] {
  override def apply(enumVal: SenateElection): String = ElectionDao.idOf(enumVal).get

  override def apply(stringVal: String): SenateElection = ElectionDao.electionWithId(stringVal).get
}
