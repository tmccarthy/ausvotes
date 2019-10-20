package au.id.tmm.ausvotes.model.instances

import au.id.tmm.ausvotes.model.instances.CountStvCodecs._
import au.id.tmm.countstv.model.values.{Count, NumPapers, NumVotes, Ordinal}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateStatuses, VoteCount}
import au.id.tmm.countstv.normalisation.Preference
import org.scalatest.FlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class CountStvCodecsSpec extends FlatSpec {

  "the count codec" should "encode a count" in {
    assert(Count(1).asJson === Json.fromInt(1))
  }

  it should "decode a count" in {
    assert(Json.fromInt(1).as[Count] === Right(Count(1)))
  }

  "the ordinal codec" should "encode an ordinal" in {
    assert(Ordinal(1).asJson === Json.fromInt(1))
  }

  it should "decode an ordinal" in {
    assert(Json.fromInt(1).as[Ordinal] === Right(Ordinal(1)))
  }

  "the vote count codec" should "encode a vote count" in {
    val expectedJson = Json.obj(
      "papers" -> Json.fromInt(1),
      "votes" -> Json.fromInt(2),
    )

    assert(VoteCount(NumPapers(1), NumVotes(2)).asJson === expectedJson)
  }

  it should "decode a vote count" in {
    val json = Json.obj(
      "papers" -> Json.fromInt(1),
      "votes" -> Json.fromInt(2),
    )

    assert(json.as[VoteCount] === Right(VoteCount(NumPapers(1), NumVotes(2))))
  }

  "the candidate statuses codec" can "encode candidate statuses" in {
    val candidateStatuses = CandidateStatuses[String](
      "katyGallagher" -> CandidateStatus.Elected(Ordinal.first, Count(1)),
      "zedSeselja" -> CandidateStatus.Elected(Ordinal.second, Count(1)),
      "anthonyHanson" -> CandidateStatus.Excluded(Ordinal.first, Count(1)),
      "christinaHobbs" -> CandidateStatus.Remaining,
      "mattDonnelly" -> CandidateStatus.Ineligible,
    )

    val expectedJson = Json.arr(
      Json.obj(
        "candidate" -> "katyGallagher".asJson,
        "outcome" -> Json.obj(
          "status" -> Json.fromString("elected"),
          "ordinal" -> Json.fromInt(0),
          "count" -> Json.fromInt(1),
        )
      ),
      Json.obj(
        "candidate" -> "zedSeselja".asJson,
        "outcome" -> Json.obj(
          "status" -> Json.fromString("elected"),
          "ordinal" -> Json.fromInt(1),
          "count" -> Json.fromInt(1),
        )
      ),
      Json.obj(
        "candidate" -> "christinaHobbs".asJson,
        "outcome" -> Json.obj(
          "status" -> Json.fromString("remaining"),
        ),
      ),
      Json.obj(
        "candidate" -> "anthonyHanson".asJson,
        "outcome" -> Json.obj(
          "status" -> Json.fromString("excluded"),
          "ordinal" -> Json.fromInt(0),
          "count" -> Json.fromInt(1),
        )
      ),
      Json.obj(
        "candidate" -> "mattDonnelly".asJson,
        "outcome" -> Json.obj(
          "status" -> Json.fromString("ineligible"),
        ),
      ),
    )

    assert(candidateStatuses.asJson === expectedJson)
  }

  it can "decode candidate statuses" in {
    val json = Json.arr(
      Json.obj(
        "candidate" -> "katyGallagher".asJson,
        "outcome" -> Json.obj(
          "status" -> Json.fromString("elected"),
          "ordinal" -> Json.fromInt(0),
          "count" -> Json.fromInt(1),
        )
      ),
      Json.obj(
        "candidate" -> "zedSeselja".asJson,
        "outcome" -> Json.obj(
          "status" -> Json.fromString("elected"),
          "ordinal" -> Json.fromInt(1),
          "count" -> Json.fromInt(1),
        )
      ),
      Json.obj(
        "candidate" -> "christinaHobbs".asJson,
        "outcome" -> Json.obj(
          "status" -> Json.fromString("remaining"),
        ),
      ),
      Json.obj(
        "candidate" -> "anthonyHanson".asJson,
        "outcome" -> Json.obj(
          "status" -> Json.fromString("excluded"),
          "ordinal" -> Json.fromInt(0),
          "count" -> Json.fromInt(1),
        )
      ),
      Json.obj(
        "candidate" -> "mattDonnelly".asJson,
        "outcome" -> Json.obj(
          "status" -> Json.fromString("ineligible"),
        ),
      ),
    )

    val expectedCandidateStatuses = CandidateStatuses[String](
      "katyGallagher" -> CandidateStatus.Elected(Ordinal.first, Count(1)),
      "zedSeselja" -> CandidateStatus.Elected(Ordinal.second, Count(1)),
      "anthonyHanson" -> CandidateStatus.Excluded(Ordinal.first, Count(1)),
      "christinaHobbs" -> CandidateStatus.Remaining,
      "mattDonnelly" -> CandidateStatus.Ineligible,
    )

    assert(json.as[CandidateStatuses[String]] === Right(expectedCandidateStatuses))
  }

  "the candidate status codec" should "fail to decode an invalid candidate status" in {
    val json = Json.obj(
      "status" -> Json.fromString("invalid"),
    )

    assert(json.as[CandidateStatus].left.map(_.message) === Left("Invalid candidate status \"invalid\""))
  }

  "a numbered preference" can "be encoded to json" in {
    assert((Preference.Numbered(42): Preference).asJson === Json.fromInt(42))
  }

  it can "be decoded from json" in {
    assert(Json.fromInt(42).as[Preference] === Right(Preference.Numbered(42)))
  }

  "a ticked preference" can "be encoded to json" in {
    assert((Preference.Tick: Preference).asJson === Json.fromString("✓"))
  }

  it can "be decoded from json" in {
    assert(Json.fromString("✓").as[Preference] === Right(Preference.Tick))
  }

  "a crossed preference" can "be encoded to json" in {
    assert((Preference.Cross: Preference).asJson === Json.fromString("x"))
  }

  it can "be decoded from json" in {
    assert(Json.fromString("x").as[Preference] === Right(Preference.Cross))
  }

  "an invalid preference" should "fail to decode" in {
    assert(Json.fromString("invalid").as[Preference].left.map(_.message) === Left("""Invalid preference "invalid""""))
  }

}
