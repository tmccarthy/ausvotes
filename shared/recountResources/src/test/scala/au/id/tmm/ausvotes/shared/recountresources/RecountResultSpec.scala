package au.id.tmm.ausvotes.shared.recountresources

import argonaut.Argonaut._
import au.id.tmm.ausvotes.core.fixtures.CandidateFixture
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.codecs.CandidateCodec._
import au.id.tmm.ausvotes.core.model.codecs.PartyCodec._
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, Name}
import au.id.tmm.ausvotes.shared.recountresources.RecountResultSpec._
import au.id.tmm.countstv.model.values.{Count, Ordinal}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateStatuses}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountResultSpec extends ImprovedFlatSpec {

  "a recount result" can "be encoded to json" in {

    val recountResult = recountResultFixture

    val expectedEncode = jObjectFields(
      "election" -> jString("2016"),
      "state" -> jString("ACT"),
      "numVacancies" -> jNumber(2),
      "ineligibleCandidates" -> jArrayElements(mattDonnelly.asJson),
      "outcomePossibilities" -> jArrayElements(
        jObjectFields(
          "probability" -> jString("1/2"),
          "outcome" -> jArrayElements(
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
        jObjectFields(
          "probability" -> jString("1/2"),
          "outcome" -> jArrayElements(
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
    )

    assert(recountResult.asJson === expectedEncode)
  }
}

object RecountResultSpec {
  val candidateFixture: CandidateFixture.ACT.type = CandidateFixture.ACT

  val katyGallagher: Candidate = candidateFixture.candidateWithName(Name("Katy", "GALLAGHER"))
  val zedSeselja: Candidate = candidateFixture.candidateWithName(Name("Zed", "SESELJA"))
  val christinaHobbs: Candidate = candidateFixture.candidateWithName(Name("Christina", "HOBBS"))
  val mattDonnelly: Candidate = candidateFixture.candidateWithName(Name("Matt", "DONNELLY"))
  val anthonyHanson: Candidate = candidateFixture.candidateWithName(Name("Anthony", "HANSON"))

  val recountResultFixture = RecountResult(
    election = SenateElection.`2016`,
    state = State.ACT,
    numVacancies = 2,
    ineligibleCandidates = Set(mattDonnelly),
    ProbabilityMeasure.evenly(
      CandidateStatuses[Candidate](
        katyGallagher -> CandidateStatus.Elected(Ordinal.first, Count(1)),
        zedSeselja -> CandidateStatus.Elected(Ordinal.second, Count(1)),
        anthonyHanson -> CandidateStatus.Excluded(Ordinal.first, Count(1)),
        christinaHobbs -> CandidateStatus.Remaining,
        mattDonnelly -> CandidateStatus.Ineligible,
      ),
      CandidateStatuses[Candidate](
        katyGallagher -> CandidateStatus.Elected(Ordinal.second, Count(1)),
        zedSeselja -> CandidateStatus.Elected(Ordinal.first, Count(1)),
        anthonyHanson -> CandidateStatus.Excluded(Ordinal.first, Count(1)),
        christinaHobbs -> CandidateStatus.Remaining,
        mattDonnelly -> CandidateStatus.Ineligible,
      )
    )
  )
}
