package au.id.tmm.ausvotes.shared.recountresources

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.codecs.CandidateCodec.aecCandidateIdCodec
import au.id.tmm.ausvotes.core.model.codecs.GeneralCodecs._
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.utilities.geo.australia.State

final case class RecountRequest(
                                 election: SenateElection,
                                 state: State,
                                 vacancies: Int,
                                 ineligibleCandidateAecIds: Set[AecCandidateId],
                                 doRounding: Boolean,
                               )

object RecountRequest {

  implicit val codec: CodecJson[RecountRequest] =
    casecodec5(apply, unapply)("election", "state", "vacancies", "ineligibleCandidates", "doRounding")

}
