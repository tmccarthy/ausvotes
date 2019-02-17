package au.id.tmm.ausvotes.model.stv

import au.id.tmm.ausvotes.model.CandidateDetails
import io.circe.{Decoder, Encoder}

final case class StvCandidate[E](
                                  election: E,
                                  candidateDetails: CandidateDetails[E],
                                  position: CandidatePosition[E],
                                )

object StvCandidate {

  implicit def ordering[E : Ordering, C]: Ordering[StvCandidate[E]] = Ordering.by(_.position)

  implicit def encoder[E : Encoder]: Encoder[StvCandidate[E]] = Encoder.forProduct3("election", "candidateDetails", "position")(c => (c.election, c.candidateDetails, c.position))
  implicit def decoder[E](implicit
                          electionDecoder: Decoder[E],
                          candidatePositionDecoder: Decoder[CandidatePosition[E]],
                         ): Decoder[StvCandidate[E]] = Decoder.forProduct3("election", "candidateDetails", "position")(StvCandidate.apply)

}
