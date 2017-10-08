package au.id.tmm.ausvotes.api.persistence.daos.rowentities

import au.id.tmm.ausvotes.api.persistence.daos.enumconverters.JurisdictionLevelEnumConverter
import au.id.tmm.ausvotes.core.model.parsing.JurisdictionLevel
import au.id.tmm.utilities.collection.Rank
import scalikejdbc._

private[daos] final case class RankRow(
                                        id: Long,
                                        stat: Long,
                                        jurisdictionLevel: JurisdictionLevel[_],
                                        ordinal: Int,
                                        ordinalIsShared: Boolean,
                                        ordinalPerCapita: Option[Int],
                                        ordinalPerCapitaIsShared: Option[Boolean],
                                        totalCount: Int,
                                      ) {

  def rank: Rank = Rank(ordinal, totalCount, rankIsShared = ordinalIsShared)

  def rankPerCapita: Option[Rank] = {
    for {
      ordinalPerCapita <- this.ordinalPerCapita
      ordinalPerCapitaIsShared <- this.ordinalPerCapitaIsShared
    } yield Rank(ordinalPerCapita, totalCount, ordinalPerCapitaIsShared)
  }

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
      ordinalIsShared = rs.boolean(r.ordinalIsShared),
      ordinalPerCapita = rs.intOpt(r.ordinalPerCapita),
      ordinalPerCapitaIsShared = rs.booleanOpt(r.ordinalPerCapitaIsShared),
      totalCount = rs.int(r.totalCount),
    )
  }

  def opt(r: SyntaxProvider[RankRow])(rs: WrappedResultSet): Option[RankRow] = {
    rs.longOpt(r.resultName.id).map(_ => RankRow(r)(rs))
  }
}