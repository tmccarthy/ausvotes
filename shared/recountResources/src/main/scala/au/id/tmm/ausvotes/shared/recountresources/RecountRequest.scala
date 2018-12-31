package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.model.Candidate
import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import io.circe.{Decoder, Encoder}

final case class RecountRequest(
                                 election: SenateElectionForState,
                                 vacancies: Int,
                                 ineligibleCandidateAecIds: Set[Candidate.Id],
                                 doRounding: Boolean,
                               )

object RecountRequest {

  implicit val encoder: Encoder[RecountRequest] = Encoder.forProduct4("election", "vacancies", "ineligibleCandidates", "doRounding")(c => (c.election, c.vacancies, c.ineligibleCandidateAecIds, c.doRounding))
  implicit val decoder: Decoder[RecountRequest] = Decoder.forProduct4("election", "vacancies", "ineligibleCandidates", "doRounding")(RecountRequest.apply)

}
