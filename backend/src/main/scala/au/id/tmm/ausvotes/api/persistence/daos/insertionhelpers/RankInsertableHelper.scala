package au.id.tmm.ausvotes.api.persistence.daos.insertionhelpers

import au.id.tmm.ausvotes.api.persistence.daos.enumconverters.JurisdictionLevelEnumConverter
import au.id.tmm.ausvotes.api.persistence.daos.insertionhelpers.InsertableSupport.Insertable
import au.id.tmm.ausvotes.core.model.parsing.JurisdictionLevel
import au.id.tmm.utilities.collection.Rank

private[daos] object RankInsertableHelper {

  def toInsertable(statRowId: Long,
                   jurisdictionLevel: JurisdictionLevel[_],
                   rank: Rank,
                   rankPerCapita: Option[Rank],
                  ): Insertable = {
    Seq(
      'stat -> statRowId,
      'jurisdiction_level -> JurisdictionLevelEnumConverter(jurisdictionLevel),
      'ordinal -> rank.ordinal,
      'ordinal_is_shared -> rank.rankIsShared,
      'ordinal_per_capita -> rankPerCapita.map(_.ordinal).orNull,
      'ordinal_per_capita_is_shared -> rankPerCapita.map(_.rankIsShared).orNull,
      'total_count -> rank.totalCount,
    )
  }

}
