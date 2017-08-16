package au.id.tmm.senatedb.api.persistence.daos.insertionhelpers

import java.sql.Connection

import au.id.tmm.senatedb.api.persistence.daos.insertionhelpers.InsertableSupport.Insertable
import au.id.tmm.utilities.geo.australia.Address

private[daos] object AddressInsertableHelper {

  def toInsertable(connection: Connection)(address: Address): Insertable = {
    val addressLines = connection.createArrayOf("TEXT", address.lines.toArray)

    Seq(
      'lines -> addressLines,
      'suburb -> address.suburb,
      'postcode -> address.postcode.code,
      'state -> address.state.abbreviation
    )
  }

  def toInsertableTuple(connection: Connection)(address: Address): Seq[Any] = {
    toInsertable(connection)(address).map { case (symbol, value) =>
      value
    }
  }

}
