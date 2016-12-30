package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.utilities.geo.australia.{Address, State}
import com.google.inject.{ImplementedBy, Inject, Singleton}
import scalikejdbc.{WrappedResultSet, _}

@ImplementedBy(classOf[ConcreteAddressDao])
trait AddressDao {

  def writeInSession(addresses: Iterable[Address])(implicit session: DBSession): Map[Address, Long]

  def rowToAddress(row: WrappedResultSet, prefix: String = ""): Address

}

@Singleton
class ConcreteAddressDao @Inject() (postcodeFlyweight: PostcodeFlyweight) extends AddressDao {

  override def writeInSession(addresses: Iterable[Address])(implicit session: DBSession): Map[Address, Long] = {
    val rowsToInsert = addresses.map(addressToRow).toSeq

    val statement = sql"INSERT INTO address(lines, suburb, postcode, state) VALUES ({lines}, {suburb}, {postcode}, {state})"
      .batchByName(rowsToInsert: _*)

    val idsForInserted: Vector[Int] = statement.apply()

    val idsForInsertedLookup = (addresses zip idsForInserted)
      .map {
        case (address, indexInDb) => address -> indexInDb.toLong
      }
      .toMap

    idsForInsertedLookup
  }

  override def rowToAddress(row: WrappedResultSet, prefix: String): Address = {
    def actualKeyFor(key: String) = if (prefix.isEmpty) key else s"$prefix.$key"

    val lines = row.array(actualKeyFor("lines")).asInstanceOf[Array[String]].toVector
    val suburb = row.string(actualKeyFor("suburb"))
    val postcode = postcodeFlyweight(row.string(actualKeyFor("postcode")))
    val state = State.fromAbbreviation(row.string(actualKeyFor("state"))).get

    Address(lines, suburb, postcode, state)
  }

  private def addressToRow(address: Address): Seq[(Symbol, Any)] = {
    Seq(
      Symbol("lines") -> address.lines.toArray,
      Symbol("suburb") -> address.suburb,
      Symbol("postcode") -> address.postcode.code,
      Symbol("state") -> address.state.abbreviation
    )
  }
}