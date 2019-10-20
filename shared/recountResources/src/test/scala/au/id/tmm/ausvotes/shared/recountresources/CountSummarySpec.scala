package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.core.fixtures.CandidateFixture
import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateElection}
import au.id.tmm.ausvotes.model.instances.CountStvCodecs._
import au.id.tmm.ausvotes.shared.recountresources.CountSummarySpec._
import au.id.tmm.countstv.model.values.{Count, Ordinal}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateStatuses, VoteCount}
import au.id.tmm.utilities.collection.DupelessSeq
import au.id.tmm.ausgeo.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import org.scalatest.FlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class CountSummarySpec extends FlatSpec {

  import CountSummarySpec.candidateFixture._

  "a recount result" can "be encoded to json" in {

    val recountResult = recountResultFixture

    val expectedEncode = Json.obj(
      "request" -> Json.obj(
        "election" -> Json.obj(
          "election" -> Json.fromString("2016"),
          "state" -> Json.fromString("ACT"),
        ),
        "numVacancies" -> Json.fromInt(2),
        "ineligibleCandidates" -> Json.arr(mattDonnelly.asJson),
        "doRounding" -> Json.True,
      ),
      "outcomePossibilities" -> Json.arr(
        Json.obj(
          "probability" -> Json.fromString("1/2"),
          "outcome" -> Json.obj(
            "elected" -> Json.arr(katyGallagher.asJson, zedSeselja.asJson),
            "exhaustedVotes" -> VoteCount.zero.asJson,
            "roundingError" -> VoteCount.zero.asJson,
            "candidateOutcomes" -> Json.arr(
              Json.obj(
                "candidate" -> katyGallagher.asJson,
                "outcome" -> Json.obj(
                  "status" -> Json.fromString("elected"),
                  "ordinal" -> Json.fromInt(0),
                  "count" -> Json.fromInt(1),
                )
              ),
              Json.obj(
                "candidate" -> zedSeselja.asJson,
                "outcome" -> Json.obj(
                  "status" -> Json.fromString("elected"),
                  "ordinal" -> Json.fromInt(1),
                  "count" -> Json.fromInt(1),
                )
              ),
              Json.obj(
                "candidate" -> christinaHobbs.asJson,
                "outcome" -> Json.obj(
                  "status" -> Json.fromString("remaining"),
                ),
              ),
              Json.obj(
                "candidate" -> anthonyHanson.asJson,
                "outcome" -> Json.obj(
                  "status" -> Json.fromString("excluded"),
                  "ordinal" -> Json.fromInt(0),
                  "count" -> Json.fromInt(1),
                )
              ),
              Json.obj(
                "candidate" -> mattDonnelly.asJson,
                "outcome" -> Json.obj(
                  "status" -> Json.fromString("ineligible"),
                ),
              ),
            ),
          ),
        ),
        Json.obj(
          "probability" -> Json.fromString("1/2"),
          "outcome" -> Json.obj(
            "elected" -> Json.arr(zedSeselja.asJson, katyGallagher.asJson),
            "exhaustedVotes" -> VoteCount.zero.asJson,
            "roundingError" -> VoteCount.zero.asJson,
            "candidateOutcomes" -> Json.arr(
              Json.obj(
                "candidate" -> zedSeselja.asJson,
                "outcome" -> Json.obj(
                  "status" -> Json.fromString("elected"),
                  "ordinal" -> Json.fromInt(0),
                  "count" -> Json.fromInt(1),
                )
              ),
              Json.obj(
                "candidate" -> katyGallagher.asJson,
                "outcome" -> Json.obj(
                  "status" -> Json.fromString("elected"),
                  "ordinal" -> Json.fromInt(1),
                  "count" -> Json.fromInt(1),
                )
              ),
              Json.obj(
                "candidate" -> christinaHobbs.asJson,
                "outcome" -> Json.obj(
                  "status" -> Json.fromString("remaining"),
                ),
              ),
              Json.obj(
                "candidate" -> anthonyHanson.asJson,
                "outcome" -> Json.obj(
                  "status" -> Json.fromString("excluded"),
                  "ordinal" -> Json.fromInt(0),
                  "count" -> Json.fromInt(1),
                )
              ),
              Json.obj(
                "candidate" -> mattDonnelly.asJson,
                "outcome" -> Json.obj(
                  "status" -> Json.fromString("ineligible"),
                ),
              ),
            ),
          ),
        ),
      ),
    )

    assert(recountResult.asJson === expectedEncode)
  }
}

object CountSummarySpec {
  val candidateFixture: CandidateFixture.ACT.type = CandidateFixture.ACT

  import candidateFixture._

  val recountResultFixture = CountSummary(
    CountSummary.Request(
      election = SenateElection.`2016`.electionForState(State.ACT).get,
      numVacancies = 2,
      ineligibleCandidates = Set(mattDonnelly),
      doRounding = true,
    ),
    ProbabilityMeasure.evenly(
      CountSummary.Outcome(
        elected = DupelessSeq(katyGallagher),
        exhaustedVotes = VoteCount.zero,
        roundingError = VoteCount.zero,
        CandidateStatuses[SenateCandidate](
          katyGallagher -> CandidateStatus.Elected(Ordinal.first, Count(1)),
          zedSeselja -> CandidateStatus.Elected(Ordinal.second, Count(1)),
          anthonyHanson -> CandidateStatus.Excluded(Ordinal.first, Count(1)),
          christinaHobbs -> CandidateStatus.Remaining,
          mattDonnelly -> CandidateStatus.Ineligible,
        ),
      ),
      CountSummary.Outcome(
        elected = DupelessSeq(katyGallagher),
        exhaustedVotes = VoteCount.zero,
        roundingError = VoteCount.zero,
        CandidateStatuses[SenateCandidate](
          katyGallagher -> CandidateStatus.Elected(Ordinal.second, Count(1)),
          zedSeselja -> CandidateStatus.Elected(Ordinal.first, Count(1)),
          anthonyHanson -> CandidateStatus.Excluded(Ordinal.first, Count(1)),
          christinaHobbs -> CandidateStatus.Remaining,
          mattDonnelly -> CandidateStatus.Ineligible,
        ),
      ),
    ),
  )
}
