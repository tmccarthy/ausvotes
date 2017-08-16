package au.id.tmm.senatedb.api.persistence.daos.rowentities

import au.id.tmm.senatedb.api.persistence.daos.ElectionDao
import au.id.tmm.senatedb.api.persistence.daos.rowentities.VoteCollectionPointRow.VcpType
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint.SpecialVoteCollectionPoint
import au.id.tmm.utilities.geo.australia.State
import scalikejdbc._

final case class SpecialVcpRow(
                                id: Long,
                                election: SenateElection,
                                state: State,
                                division: DivisionRow,

                                voteCollectionPointType: VcpType,
                                name: String,
                                number: Int,
                              ) extends VoteCollectionPointRow {
  override def asVoteCollectionPoint: SpecialVoteCollectionPoint = {
    voteCollectionPointType match {
      case VcpType.Absentee => VoteCollectionPoint.Absentee(election, state, division.asDivision, number)
      case VcpType.Postal => VoteCollectionPoint.Postal(election, state, division.asDivision, number)
      case VcpType.Prepoll => VoteCollectionPoint.PrePoll(election, state, division.asDivision, number)
      case VcpType.Provisional => VoteCollectionPoint.Provisional(election, state, division.asDivision, number)
    }
  }
}

object SpecialVcpRow extends SQLSyntaxSupport[SpecialVcpRow] {

  override def tableName: String = "special_vote_collection_point"

  def apply(v: SyntaxProvider[SpecialVcpRow],
            d: SyntaxProvider[DivisionRow],
           )(rs: WrappedResultSet): SpecialVcpRow =
    apply(v.resultName, d.resultName)(rs)

  def apply(v: ResultName[SpecialVcpRow],
            d: ResultName[DivisionRow],
           )(rs: WrappedResultSet): SpecialVcpRow = {
    SpecialVcpRow(
      id = rs.long(v.id),
      election = ElectionDao.electionWithId(rs.string(v.election)).get,
      state = State.fromAbbreviation(rs.string(v.state)).get,
      division = DivisionRow(d)(rs),
      voteCollectionPointType = VcpType.parse(rs.string(v.voteCollectionPointType)),
      name = rs.string(v.name),
      number = rs.int(v.number),
    )
  }

  def opt(v: ResultName[SpecialVcpRow],
          d: ResultName[DivisionRow],
         )(rs: WrappedResultSet): Option[SpecialVcpRow] = {
    rs.longOpt(v.id).map(_ => SpecialVcpRow(v, d)(rs))
  }
}