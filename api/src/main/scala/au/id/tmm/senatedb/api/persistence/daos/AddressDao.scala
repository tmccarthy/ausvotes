package au.id.tmm.senatedb.api.persistence.daos

import java.sql.Connection

import au.id.tmm.senatedb.api.persistence.daos.rowentities.AddressRow
import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.utilities.geo.australia.Address
import com.google.inject.{ImplementedBy, Inject, Singleton}
import scalikejdbc._

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

    val a = AddressRow.syntax

    withSQL(select.from(AddressRow as a).where.eq(a.id, id).limit(1))
      .map(AddressRow(postcodeFlyweight, a))
      .headOption()
      .apply()
      .map(_.asAddress)
  }
}

private[daos] object AddressRowConversions extends RowConversions {

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