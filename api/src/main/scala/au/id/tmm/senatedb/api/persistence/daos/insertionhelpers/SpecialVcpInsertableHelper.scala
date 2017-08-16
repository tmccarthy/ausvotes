package au.id.tmm.senatedb.api.persistence.daos.insertionhelpers

import au.id.tmm.senatedb.api.persistence.daos.ElectionDao
import au.id.tmm.senatedb.api.persistence.daos.insertionhelpers.InsertableSupport.Insertable
import au.id.tmm.senatedb.api.persistence.daos.rowentities.VoteCollectionPointRow.VcpType
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint._
import au.id.tmm.utilities.hashing.Pairing

private[daos] object SpecialVcpInsertableHelper {

  def idOf(specialVcp: SpecialVoteCollectionPoint): Long = {
    val electionCode = specialVcp.election.aecID

    val vcpTypeCode = specialVcp match {
      case _: Absentee => 1
      case _: Postal => 2
      case _: PrePoll => 3
      case _: Provisional => 4
    }

    val number = specialVcp.number

    Pairing.Szudzik.combine(electionCode, vcpTypeCode, number)
  }

  def toInsertable(specialVcp: SpecialVoteCollectionPoint): Insertable = {
    Seq(
      'id -> idOf(specialVcp),
      'election -> ElectionDao.idOf(specialVcp.election).get,
      'state -> specialVcp.state.abbreviation,
      'division -> DivisionInsertableHelper.idOf(specialVcp.division),
      'vote_collection_point_type -> VcpType.asString(VcpType.of(specialVcp)),
      'name -> specialVcp.name,
      'number -> specialVcp.number,
    )
  }
}
