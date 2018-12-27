package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausvotes.model.instances.StateInstances.codec
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class ElectorateSpec extends ImprovedFlatSpec {

  private val boothby = Electorate[FederalElection, State](FederalElection.`2016`, State.SA, "Boothby", Electorate.Id(0))
  private val mayo = Electorate[FederalElection, State](FederalElection.`2016`, State.SA, "Mayo", Electorate.Id(0))
  private val wills = Electorate[FederalElection, State](FederalElection.`2016`, State.VIC, "Wills", Electorate.Id(0))
  private val boothby2013 = Electorate[FederalElection, State](FederalElection.`2016`, State.SA, "Boothby", Electorate.Id(0))

  "an stv candidate" can "be encoded to json" in {
    val electorate = boothby

    val json = Json.obj(
      "election" -> electorate.election.asJson,
      "jurisdiction" -> electorate.jurisdiction.asJson,
      "name" -> electorate.name.asJson,
      "id" -> electorate.id.asJson,
    )

    assert(electorate.asJson === json)
  }

  it can "be decoded from json" in {
    val electorate = boothby

    val json = Json.obj(
      "election" -> electorate.election.asJson,
      "jurisdiction" -> electorate.jurisdiction.asJson,
      "name" -> electorate.name.asJson,
      "id" -> electorate.id.asJson,
    )

    assert(json.as[Electorate[FederalElection, State]] === Right(electorate))
  }

  it should "have an ordering" in {
    val electorates = List(wills, mayo, boothby2013, boothby)

    val sorted = List(boothby2013, boothby, mayo, wills)

    assert(electorates.sorted === sorted)
  }

}
