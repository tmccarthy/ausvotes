package au.id.tmm.senatedb.api.persistence.daos.rowentities

import au.id.tmm.senatedb.api.persistence.daos.enumconverters.JurisdictionLevelEnumConverter
import au.id.tmm.senatedb.api.persistence.entities.stats.Rank
import au.id.tmm.senatedb.core.model.parsing.JurisdictionLevel
import scalikejdbc._

private[daos] final case class RankRow(
                                        id: Long,
                                        stat: Long,
                                        jurisdictionLevel: JurisdictionLevel[_],
                                        ordinal: Int,
                                        ordinalPerCapita: Option[Int],
                                        totalCount: Int,
                                      ) {

  def rank: Rank = Rank(ordinal, totalCount)

  def rankPerCapita: Option[Rank] = ordinalPerCapita.map(Rank(_, totalCount))

}

private[daos] object RankRow extends SQLSyntaxSupport[RankRow] {

  override def tableName: String = "rank"

  def apply(r: SyntaxProvider[RankRow])(rs: WrappedResultSet): RankRow = apply(r.resultName)(rs)

  def apply(r: ResultName[RankRow])(rs: WrappedResultSet): RankRow = {
    RankRow(
      id = rs.long(r.id),
      stat = rs.long(r.stat),
      jurisdictionLevel = JurisdictionLevelEnumConverter(rs.string(r.jurisdictionLevel)),
      ordinal = rs.int(r.ordinal),
      ordinalPerCapita = rs.intOpt(r.ordinalPerCapita),
      totalCount = rs.int(r.totalCount),
    )
  }

  def opt(r: SyntaxProvider[RankRow])(rs: WrappedResultSet): Option[RankRow] = {
    rs.longOpt(r.resultName.id).map(_ => RankRow(r)(rs))
  }
}