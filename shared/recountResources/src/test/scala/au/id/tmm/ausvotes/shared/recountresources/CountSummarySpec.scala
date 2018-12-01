package au.id.tmm.ausvotes.shared.recountresources

import argonaut.Argonaut._
import au.id.tmm.ausvotes.core.fixtures.CandidateFixture
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.codecs.CandidateCodec._
import au.id.tmm.ausvotes.core.model.codecs.GeneralCodecs._
import au.id.tmm.ausvotes.core.model.codecs.PartyCodec._
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.shared.recountresources.CountSummarySpec._
import au.id.tmm.countstv.model.values.{Count, Ordinal}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateStatuses, VoteCount}
import au.id.tmm.utilities.collection.DupelessSeq
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountSummarySpec extends ImprovedFlatSpec {

  import CountSummarySpec.candidateFixture._

  "a recount result" can "be encoded to json" in {

    val recountResult = recountResultFixture

    val expectedEncode = jObjectFields(
      "request" -> jObjectFields(
        "election" -> jString("2016"),
        "state" -> jString("ACT"),
        "numVacancies" -> jNumber(2),
        "ineligibleCandidates" -> jArrayElements(mattDonnelly.asJson),
        "doRounding" -> jTrue,
      ),
      "outcomePossibilities" -> jArrayElements(
        jObjectFields(
          "probability" -> jString("1/2"),
          "outcome" -> jObjectFields(
            "elected" -> jArrayElements(katyGallagher.asJson, zedSeselja.asJson),
            "exhaustedVotes" -> VoteCount.zero.asJson,
            "roundingError" -> VoteCount.zero.asJson,
            "candidateOutcomes" -> jArrayElements(
              jObjectFields(
                "candidate" -> katyGallagher.asJson,
                "outcome" -> jObjectFields(
                  "status" -> jString("elected"),
                  "ordinal" -> jNumber(0),
                  "count" -> jNumber(1),
                )
              ),
              jObjectFields(
                "candidate" -> zedSeselja.asJson,
                "outcome" -> jObjectFields(
                  "status" -> jString("elected"),
                  "ordinal" -> jNumber(1),
                  "count" -> jNumber(1),
                )
              ),
              jObjectFields(
                "candidate" -> christinaHobbs.asJson,
                "outcome" -> jObjectFields(
                  "status" -> jString("remaining"),
                ),
              ),
              jObjectFields(
                "candidate" -> anthonyHanson.asJson,
                "outcome" -> jObjectFields(
                  "status" -> jString("excluded"),
                  "ordinal" -> jNumber(0),
                  "count" -> jNumber(1),
                )
              ),
              jObjectFields(
                "candidate" -> mattDonnelly.asJson,
                "outcome" -> jObjectFields(
                  "status" -> jString("ineligible"),
                ),
              ),
            ),
          ),
        ),
        jObjectFields(
          "probability" -> jString("1/2"),
          "outcome" -> jObjectFields(
            "elected" -> jArrayElements(zedSeselja.asJson, katyGallagher.asJson),
            "exhaustedVotes" -> VoteCount.zero.asJson,
            "roundingError" -> VoteCount.zero.asJson,
            "candidateOutcomes" -> jArrayElements(
              jObjectFields(
                "candidate" -> zedSeselja.asJson,
                "outcome" -> jObjectFields(
                  "status" -> jString("elected"),
                  "ordinal" -> jNumber(0),
                  "count" -> jNumber(1),
                )
              ),
              jObjectFields(
                "candidate" -> katyGallagher.asJson,
                "outcome" -> jObjectFields(
                  "status" -> jString("elected"),
                  "ordinal" -> jNumber(1),
                  "count" -> jNumber(1),
                )
              ),
              jObjectFields(
                "candidate" -> christinaHobbs.asJson,
                "outcome" -> jObjectFields(
                  "status" -> jString("remaining"),
                ),
              ),
              jObjectFields(
                "candidate" -> anthonyHanson.asJson,
                "outcome" -> jObjectFields(
                  "status" -> jString("excluded"),
                  "ordinal" -> jNumber(0),
                  "count" -> jNumber(1),
                )
              ),
              jObjectFields(
                "candidate" -> mattDonnelly.asJson,
                "outcome" -> jObjectFields(
                  "status" -> jString("ineligible"),
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
      election = SenateElection.`2016`,
      state = State.ACT,
      numVacancies = 2,
      ineligibleCandidates = Set(mattDonnelly),
      doRounding = true,
    ),
    ProbabilityMeasure.evenly(
      CountSummary.Outcome(
        elected = DupelessSeq(katyGallagher),
        exhaustedVotes = VoteCount.zero,
        roundingError = VoteCount.zero,
        CandidateStatuses[Candidate](
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
        CandidateStatuses[Candidate](
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
