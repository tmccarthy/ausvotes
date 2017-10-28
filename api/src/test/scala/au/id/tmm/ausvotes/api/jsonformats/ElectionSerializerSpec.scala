package au.id.tmm.ausvotes.api.jsonformats

import au.id.tmm.ausvotes.backend.persistence.daos.ElectionDao
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.json4s.JsonDSL._
import org.json4s.{Extraction, _}

class ElectionSerializerSpec extends ImprovedFlatSpec {

  implicit val formats = ApiFormats

  "the election serializer" can "serialise an election to json" in {

    val actualJson = Extraction.decompose(SenateElection.`2016`)(formats)

    val expectedJson =
      ("date" -> Extraction.decompose(SenateElection.`2016`.date)) ~
        ("states" -> Extraction.decompose(SenateElection.`2016`.states)) ~
        ("name" -> JString(SenateElection.`2016`.name)) ~
        ("id" -> JString(ElectionDao.idOf(SenateElection.`2016`)))

    assert(actualJson === expectedJson)
  }

  it can "deserialise an election from json" in {

    val inputJson = pair2jvalue("id" -> ElectionDao.idOf(SenateElection.`2016`))

    val actualElection = Extraction.extract[SenateElection](inputJson)

    assert(actualElection === SenateElection.`2016`)
  }

}
