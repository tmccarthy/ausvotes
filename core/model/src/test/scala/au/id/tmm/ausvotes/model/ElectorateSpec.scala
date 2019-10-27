package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausgeo.Codecs._
import au.id.tmm.ausgeo.State
import io.circe.Json
import io.circe.syntax.EncoderOps
import org.scalatest.FlatSpec

class ElectorateSpec extends FlatSpec {

  private val boothby = Electorate[FederalElection, State](FederalElection.`2016`, State.SA, "Boothby")
  private val mayo = Electorate[FederalElection, State](FederalElection.`2016`, State.SA, "Mayo")
  private val wills = Electorate[FederalElection, State](FederalElection.`2016`, State.VIC, "Wills")
  private val boothby2013 = Electorate[FederalElection, State](FederalElection.`2013`, State.SA, "Boothby")

  "an stv candidate" can "be encoded to json" in {
    val electorate = boothby

    val json = Json.obj(
      "election" -> electorate.election.asJson,
      "jurisdiction" -> electorate.jurisdiction.asJson,
      "name" -> electorate.name.asJson,
    )

    assert(electorate.asJson === json)
  }

  it can "be decoded from json" in {
    val electorate = boothby

    val json = Json.obj(
      "election" -> electorate.election.asJson,
      "jurisdiction" -> electorate.jurisdiction.asJson,
      "name" -> electorate.name.asJson,
    )

    assert(json.as[Electorate[FederalElection, State]] === Right(electorate))
  }

  it should "have an ordering" in {
    val electorates = List(wills, mayo, boothby2013, boothby)

    val sorted = List(boothby2013, wills, boothby, mayo)

    assert(electorates.sorted === sorted)
  }

}
