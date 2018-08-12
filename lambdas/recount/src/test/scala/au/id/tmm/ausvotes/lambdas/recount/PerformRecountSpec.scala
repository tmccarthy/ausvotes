package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import argonaut.CodecJson
import au.id.tmm.ausvotes.core.fixtures.{BallotMaker, CandidateFixture}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.codecs.{CandidateCodec, PartyCodec}
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, Name}
import au.id.tmm.countstv.model.preferences.PreferenceTree
import au.id.tmm.countstv.model.values.{Count, Ordinal}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateStatuses}
import au.id.tmm.utilities.collection.DupelessSeq
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PerformRecountSpec extends ImprovedFlatSpec {

  private val candidateFixture = CandidateFixture.ACT
  private val ballotMaker = BallotMaker(candidateFixture)

  private val katyGallagher = candidateFixture.candidateWithName(Name("Katy", "GALLAGHER"))
  private val zedSeselja = candidateFixture.candidateWithName(Name("Zed", "SESELJA"))
  private val christinaHobbs = candidateFixture.candidateWithName(Name("Christina", "HOBBS"))
  private val mattDonnelly = candidateFixture.candidateWithName(Name("Matt", "DONNELLY"))
  private val anthonyHanson = candidateFixture.candidateWithName(Name("Anthony", "HANSON"))

  "performing a recount" should "successfully complete a count" in {

    val ballotPapers = candidateFixture.candidates.toVector.flatMap { candidate =>
      val btlPosition = candidate.btlPosition

      if (candidate == katyGallagher) {
        Vector.fill(100)(Vector(btlPosition))
      } else if (candidate == zedSeselja) {
        Vector.fill(99)(Vector(btlPosition))
      } else {
        Vector.fill(0)(Vector(btlPosition))
      }
    }

    val preferenceTree = PreferenceTree.from(allCandidates = candidateFixture.candidates.map(_.btlPosition))(ballotPapers)

    val actualResult = PerformRecount.performRecount(
      election = SenateElection.`2016`,
      state = State.ACT,
      allCandidates = candidateFixture.candidates,
      preferenceTree = preferenceTree,
      ineligibleCandidates = Set.empty,
      numVacancies = 2,
    )

    assert(actualResult.map(_.candidateOutcomeProbabilities.onlyOutcome.electedCandidates) === Right(DupelessSeq(
      katyGallagher,
      zedSeselja,
    )))
  }

  "a recount result" can "be encoded to json" in {
    implicit val partyCodec: PartyCodec = PartyCodec()
    implicit val candidateCodec: CodecJson[Candidate] = CandidateCodec(candidateFixture.groupFixture.groups)

    val recountResult = PerformRecount.Result(
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

    val expectedEncode = jArrayElements(
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
    )

    assert(recountResult.asJson === expectedEncode)
  }

}
