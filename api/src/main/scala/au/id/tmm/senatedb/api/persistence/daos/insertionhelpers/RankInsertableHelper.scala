package au.id.tmm.senatedb.api.persistence.daos.insertionhelpers

import au.id.tmm.senatedb.api.persistence.daos.enumconverters.JurisdictionLevelEnumConverter
import au.id.tmm.senatedb.api.persistence.daos.insertionhelpers.InsertableSupport.Insertable
import au.id.tmm.senatedb.api.persistence.entities.stats.Rank
import au.id.tmm.senatedb.core.model.parsing.JurisdictionLevel

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
      'ordinal_per_capita -> rankPerCapita.map(_.ordinal).orNull,
      'total_count -> rank.totalCount,
    )
  }

}
