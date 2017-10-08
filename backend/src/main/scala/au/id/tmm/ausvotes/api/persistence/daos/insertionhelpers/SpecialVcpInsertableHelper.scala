package au.id.tmm.ausvotes.api.persistence.daos.insertionhelpers

import au.id.tmm.ausvotes.api.persistence.daos.enumconverters.{ElectionEnumConverter, VcpTypeEnumConverter}
import au.id.tmm.ausvotes.api.persistence.daos.insertionhelpers.InsertableSupport.Insertable
import au.id.tmm.ausvotes.api.persistence.daos.rowentities.VoteCollectionPointRow.VcpType
import au.id.tmm.ausvotes.core.model.parsing.VoteCollectionPoint._
import au.id.tmm.utilities.hashing.Pairing

private[daos] object SpecialVcpInsertableHelper {

  def idOf(specialVcp: SpecialVoteCollectionPoint): Long = {
    val divisionCode = DivisionInsertableHelper.idOf(specialVcp.division)

    val vcpCode = {
      val vcpTypeCode = specialVcp match {
        case _: Absentee => 1
        case _: Postal => 2
        case _: PrePoll => 3
        case _: Provisional => 4
      }

      val number = specialVcp.number

      Pairing.Szudzik.pair(vcpTypeCode, number)
    }

    Pairing.Szudzik.pair(divisionCode, vcpCode)
  }

  def toInsertable(specialVcp: SpecialVoteCollectionPoint): Insertable = {
    Seq(
      'id -> idOf(specialVcp),
      'election -> ElectionEnumConverter(specialVcp.election),
      'state -> specialVcp.state.abbreviation,
      'division -> DivisionInsertableHelper.idOf(specialVcp.division),
      'vote_collection_point_type -> VcpTypeEnumConverter(VcpType.of(specialVcp)),
      'name -> specialVcp.name,
      'number -> specialVcp.number,
    )
  }
}
