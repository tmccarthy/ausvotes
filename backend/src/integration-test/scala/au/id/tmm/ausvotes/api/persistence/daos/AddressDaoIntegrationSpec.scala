package au.id.tmm.ausvotes.api.persistence.daos

import au.id.tmm.ausvotes.api.integrationtest.PostgresService
import au.id.tmm.ausvotes.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.utilities.geo.australia.{Address, Postcode, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import scalikejdbc.DB

class AddressDaoIntegrationSpec extends ImprovedFlatSpec with PostgresService {

  private val sut = new ConcreteAddressDao(PostcodeFlyweight())

  "the address dao" should "return a stored address" in {
    DB.localTx { implicit session =>
      val writtenAddress = Address(
        lines = Vector("123 Fourth St"),
        suburb = "Placeville",
        postcode = Postcode("1000"),
        state = State.SA,
      )

      val writtenAddressIds = sut.writeInSession(Vector(writtenAddress))

      val writtenId = writtenAddressIds(writtenAddress)

      val returnedAddress = sut.findById(writtenId)

      assert(returnedAddress contains writtenAddress)
    }
  }

  it should "return nothing if the address cannot be found" in {
    DB.localTx { implicit session =>
      val foundAddress = sut.findById(1)

      assert(foundAddress === None)
    }
  }

}
