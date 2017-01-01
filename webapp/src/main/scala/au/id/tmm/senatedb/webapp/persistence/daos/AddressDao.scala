package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.utilities.geo.australia.{Address, State}
import com.google.inject.{ImplementedBy, Inject, Singleton}
import scalikejdbc.{WrappedResultSet, _}

@ImplementedBy(classOf[ConcreteAddressDao])
trait AddressDao {

  def writeInSession(addresses: Iterable[Address])(implicit session: DBSession): Map[Address, Long]

}

@Singleton
class ConcreteAddressDao @Inject() (postcodeFlyweight: PostcodeFlyweight) extends AddressDao {

  override def writeInSession(addresses: Iterable[Address])(implicit session: DBSession): Map[Address, Long] = {
    val rowsToInsert = addresses.map(AddressRowConversions.toRow).toSeq

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
}

private[daos] object AddressRowConversions extends RowConversions {

  protected def fromRow(postcodeFlyweight: PostcodeFlyweight, alias: String)(row: WrappedResultSet): Address = {
    val c = aliasedColumnName(alias)(_)

    val lines = row.array(c("lines")).asInstanceOf[Array[String]].toVector
    val suburb = row.string(c("suburb"))
    val postcode = postcodeFlyweight(row.string(c("postcode")))
    val state = State.fromAbbreviation(row.string(c("state"))).get

    Address(lines, suburb, postcode, state)
  }

  def toRow(address: Address): Seq[(Symbol, Any)] = {
    Seq(
      Symbol("lines") -> address.lines.toArray,
      Symbol("suburb") -> address.suburb,
      Symbol("postcode") -> address.postcode.code,
      Symbol("state") -> address.state.abbreviation
    )
  }
}