package au.id.tmm.ausvotes.api.persistence.daos

import au.id.tmm.ausvotes.api.persistence.daos.insertionhelpers.AddressInsertableHelper
import au.id.tmm.ausvotes.api.persistence.daos.rowentities.AddressRow
import au.id.tmm.ausvotes.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.utilities.geo.australia.Address
import com.google.inject.{ImplementedBy, Inject, Singleton}
import scalikejdbc._

@ImplementedBy(classOf[ConcreteAddressDao])
trait AddressDao {

  def writeInSession(addresses: Vector[Address])(implicit session: DBSession): Map[Address, Long]

  def findById(id: Long)(implicit session: DBSession): Option[Address]

}

@Singleton
class ConcreteAddressDao @Inject() (postcodeFlyweight: PostcodeFlyweight) extends AddressDao {

  override def writeInSession(addresses: Vector[Address])(implicit session: DBSession): Map[Address, Long] = {
    val insertStatement =
      sql"""INSERT INTO address(lines, suburb, postcode, state)
           |  VALUES (?, ?, ?, ?);
           |""".stripMargin

    val insertables: Seq[Seq[Any]] = addresses.map(AddressInsertableHelper.toInsertableTuple(session.connection))

    val keys: Vector[Long] = insertStatement.batchAndReturnGeneratedKey(insertables: _*).apply()

    (addresses zip keys).toMap
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
