package au.id.tmm.senatedb.api.persistence.daos.rowentities

import au.id.tmm.senatedb.api.persistence.daos.enumconverters.{ElectionEnumConverter, StateEnumConverter}
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Division
import au.id.tmm.utilities.geo.australia.State
import scalikejdbc._

private[daos] final case class DivisionRow (
                                             id: Long,
                                             election: SenateElection,
                                             aecId: Int,
                                             state: State,
                                             name: String,
                                           ) {
  def asDivision: Division = Division(election, state, name, aecId)
}

private[daos] object DivisionRow extends SQLSyntaxSupport[DivisionRow] {
  override val tableName = "division"

  def apply(d: SyntaxProvider[DivisionRow])(rs: WrappedResultSet): DivisionRow = apply(d.resultName)(rs)

  def apply(d: ResultName[DivisionRow])(rs: WrappedResultSet): DivisionRow = {
    DivisionRow(
      id = rs.long(d.id),
      election = ElectionEnumConverter(rs.string(d.election)),
      aecId = rs.int(d.aecId),
      state = StateEnumConverter(rs.string(d.state)),
      name = rs.string(d.name),
    )
  }

  def opt(d: ResultName[DivisionRow])(rs: WrappedResultSet): Option[DivisionRow] = {
    rs.longOpt(d.id).map(_ => DivisionRow(d)(rs))
  }
}
