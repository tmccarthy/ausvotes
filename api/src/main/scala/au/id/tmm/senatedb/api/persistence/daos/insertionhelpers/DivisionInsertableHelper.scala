package au.id.tmm.senatedb.api.persistence.daos.insertionhelpers

import au.id.tmm.senatedb.api.persistence.daos.enumconverters.ElectionEnumConverter
import au.id.tmm.senatedb.api.persistence.daos.insertionhelpers.InsertableSupport.Insertable
import au.id.tmm.senatedb.core.model.parsing.Division
import au.id.tmm.utilities.hashing.Pairing

private[daos] object DivisionInsertableHelper {

  def idOf(division: Division): Long = Pairing.Szudzik.pair(division.election.aecID, division.aecId)

  def toInsertable(division: Division): Insertable = {
    Seq(
      'id -> idOf(division),
      'election -> ElectionEnumConverter(division.election),
      'aec_id -> division.aecId,
      'state -> division.state.abbreviation,
      'name -> division.name
    )
  }
}
