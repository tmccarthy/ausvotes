package au.id.tmm.senatedb.api.persistence.daos

import java.sql.Connection

import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.utilities.geo.australia.{Address, State}
import com.google.inject.{ImplementedBy, Inject, Singleton}
import scalikejdbc.{WrappedResultSet, _}

@ImplementedBy(classOf[ConcreteAddressDao])
trait AddressDao {

  def writeInSession(addresses: Iterable[Address])(implicit session: DBSession): Map[Address, Long]

  def findById(id: Long)(implicit session: DBSession): Option[Address]

}

@Singleton
class ConcreteAddressDao @Inject() (postcodeFlyweight: PostcodeFlyweight) extends AddressDao {

  override def writeInSession(addresses: Iterable[Address])(implicit session: DBSession): Map[Address, Long] = {

    addresses.toStream
      .map { address =>
        val asRow = AddressRowConversions.toRow(session.connection)(address)

        val statement = sql"""INSERT INTO address(
                           |  lines,
                           |  suburb,
                           |  postcode,
                           |  state
                           |) VALUES (
                           |  ${asRow("lines")},
                           |  ${asRow("suburb")},
                           |  ${asRow("postcode")},
                           |  ${asRow("state")}
                           |)""".stripMargin
          .updateAndReturnGeneratedKey()

        val generatedId = statement.apply()

        address -> generatedId
      }
      .toMap
  }

  override def findById(id: Long)(implicit session: DBSession): Option[Address] = {
    val statement = sql"""
         |SELECT
         |  *
         |FROM address
         |WHERE id = $id
       """.stripMargin

    statement
      .map(AddressRowConversions.fromRow(postcodeFlyweight, alias = ""))
      .headOption()
      .apply()
  }
}

private[daos] object AddressRowConversions extends RowConversions {

  def fromRow(postcodeFlyweight: PostcodeFlyweight, alias: String)(row: WrappedResultSet): Address = {
    val c = aliasedColumnName(alias)(_)

    val lines = row.array(c("lines")).getArray.asInstanceOf[Array[String]].toVector
    val suburb = row.string(c("suburb"))
    val postcode = postcodeFlyweight(row.string(c("postcode")))
    val state = State.fromAbbreviation(row.string(c("state"))).get

    Address(lines, suburb, postcode, state)
  }

  def toRow(connection: Connection)(address: Address): Map[String, Any] = {
    val addressLines = connection.createArrayOf("TEXT", address.lines.toArray)

    Map(
      "lines" -> addressLines,
      "suburb" -> address.suburb,
      "postcode" -> address.postcode.code,
      "state" -> address.state.abbreviation
    )
  }
}