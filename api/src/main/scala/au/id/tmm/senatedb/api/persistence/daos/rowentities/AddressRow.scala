package au.id.tmm.senatedb.api.persistence.daos.rowentities

import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.utilities.geo.australia.{Address, Postcode, State}
import scalikejdbc._

private[daos] final case class AddressRow(
                                           id: Long,
                                           lines: Vector[String],
                                           suburb: String,
                                           postcode: Postcode,
                                           state: State,
                                         ) {
  def asAddress: Address = Address(lines, suburb, postcode, state)
}

private[daos] object AddressRow extends SQLSyntaxSupport[AddressRow] {

  override val tableName = "address"

  def apply(postcodeFlyweight: PostcodeFlyweight, a: SyntaxProvider[AddressRow])(rs: WrappedResultSet): AddressRow =
    apply(postcodeFlyweight, a.resultName)(rs)

  def apply(postcodeFlyweight: PostcodeFlyweight, a: ResultName[AddressRow])(rs: WrappedResultSet): AddressRow = {
    AddressRow(
      id = rs.long(a.id),
      lines = rs.array(a.lines).getArray.asInstanceOf[Array[String]].toVector,
      suburb = rs.string(a.suburb),
      postcode = postcodeFlyweight(rs.string(a.postcode)),
      state = State.fromAbbreviation(rs.string(a.state)).get,
    )
  }

  def opt(postcodeFlyweight: PostcodeFlyweight, a: ResultName[AddressRow])(rs: WrappedResultSet): Option[AddressRow] = {
    rs.longOpt(a.id).map(_ => AddressRow(postcodeFlyweight, a)(rs))
  }
}
