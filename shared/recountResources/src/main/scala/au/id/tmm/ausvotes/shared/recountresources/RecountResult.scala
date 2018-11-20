package au.id.tmm.ausvotes.shared.recountresources

import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.codecs.CandidateCodec._
import au.id.tmm.ausvotes.core.model.codecs.GeneralCodecs._
import au.id.tmm.ausvotes.core.model.codecs.PartyCodec._
import au.id.tmm.ausvotes.core.model.codecs.ProbabilityMeasureCodec._
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.countstv.model.CandidateStatuses
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure

// TODO call this CountResult? It's used for canonical recounts too
final case class RecountResult(
                                election: SenateElection,
                                state: State,
                                numVacancies: Int,
                                ineligibleCandidates: Set[Candidate],
                                candidateOutcomeProbabilities: ProbabilityMeasure[CandidateStatuses[Candidate]],
                              )

object RecountResult {

  implicit val encodeRecountResult: EncodeJson[RecountResult] = result =>
    jObjectFields(
      "election" -> result.election.asJson,
      "state" -> result.state.asJson,
      "numVacancies" -> result.numVacancies.asJson,
      "ineligibleCandidates" -> result.ineligibleCandidates.asJson,
      "outcomePossibilities" -> result.candidateOutcomeProbabilities.asJson,
    )

  // TODO test this
  implicit def decodeRecountResult(implicit decodeCandidates: DecodeJson[Candidate]): DecodeJson[RecountResult] =
    jdecode5L(RecountResult.apply)("election", "state", "numVacancies", "ineligibleCandidates", "outcomePossibilities")

}
