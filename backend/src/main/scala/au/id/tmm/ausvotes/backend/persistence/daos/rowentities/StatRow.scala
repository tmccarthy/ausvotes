package au.id.tmm.ausvotes.backend.persistence.daos.rowentities

import au.id.tmm.ausvotes.backend.persistence.daos.enumconverters.{ElectionEnumConverter, StatClassEnumConverter, StateEnumConverter}
import au.id.tmm.ausvotes.backend.persistence.entities.stats.{Stat, StatClass}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.ausvotes.core.model.parsing.JurisdictionLevel
import au.id.tmm.utilities.geo.australia.State
import scalikejdbc._

private[daos] final case class StatRow(
                                        id: Long,
                                        statClass: StatClass,
                                        election: SenateElection,
                                        state: Option[State],
                                        division: Option[DivisionRow],
                                        pollingPlace: Option[PollingPlaceRow],
                                        specialVcp: Option[SpecialVcpRow],
                                        amount: Double,
                                        perCapita: Option[Double],
                                        rankRows: Vector[RankRow] = Vector(),
                                      ) {
  def jurisdictionLevel: JurisdictionLevel[_] = {
    if (pollingPlace.isDefined || specialVcp.isDefined) {
      JurisdictionLevel.VoteCollectionPoint
    } else if (division.isDefined) {
      JurisdictionLevel.Division
    } else if (state.isDefined) {
      JurisdictionLevel.State
    } else {
      JurisdictionLevel.Nation
    }
  }

  def jurisdiction: AnyRef = {
    if (pollingPlace.isDefined) {
      pollingPlace.get.asVoteCollectionPoint
    } else if (specialVcp.isDefined) {
      specialVcp.get.asVoteCollectionPoint
    } else if (division.isDefined) {
      division.get.asDivision
    } else if (state.isDefined) {
      state.get
    } else {
      election
    }
  }

  def asStat[A]: Stat[A] = {
    Stat(
      statClass,
      jurisdictionLevel.asInstanceOf[JurisdictionLevel[A]],
      jurisdiction.asInstanceOf[A],
      amount,
      rankRows.map(r => r.jurisdictionLevel -> r.rank).toMap,
      perCapita,
      rankRows.flatMap(rankRow => rankRow.rankPerCapita.map(rank => rankRow.jurisdictionLevel -> rank)).toMap,
    )
  }
}

private[daos] object StatRow extends SQLSyntaxSupport[StatRow] {

  override def tableName: String = "stat"

  def apply(postcodeFlyweight: PostcodeFlyweight,
            s: SyntaxProvider[StatRow],
            d: SyntaxProvider[DivisionRow],
            p: SyntaxProvider[PollingPlaceRow],
            v: SyntaxProvider[SpecialVcpRow],
            a: SyntaxProvider[AddressRow],
           )(rs: WrappedResultSet): StatRow = {
    apply(
      postcodeFlyweight,
      s.resultName,
      d.resultName,
      p.resultName,
      v.resultName,
      a.resultName,
    )(rs)
  }

  def apply(postcodeFlyweight: PostcodeFlyweight,
            s: ResultName[StatRow],
            d: ResultName[DivisionRow],
            p: ResultName[PollingPlaceRow],
            v: ResultName[SpecialVcpRow],
            a: ResultName[AddressRow],
           )(rs: WrappedResultSet): StatRow = {
    StatRow(
      id = rs.long(s.id),
      statClass = StatClassEnumConverter(rs.string(s.statClass)),
      election = ElectionEnumConverter(rs.string(s.election)),
      state = rs.stringOpt(s.state).map(StateEnumConverter.apply),
      division = DivisionRow.opt(d)(rs),
      pollingPlace = PollingPlaceRow.opt(postcodeFlyweight, p, d, a)(rs),
      specialVcp = SpecialVcpRow.opt(v, d)(rs),
      amount = rs.double(s.amount),
      perCapita = rs.doubleOpt(s.perCapita),
    )
  }
}
