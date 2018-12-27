package au.id.tmm.ausvotes.model.stv

import io.circe.{Decoder, Encoder}

final case class StvCandidate[E, C](
                                     election: E,
                                     candidate: C,
                                     position: CandidatePosition[E],
                                   )

object StvCandidate {

  implicit def encoder[E : Encoder, C : Encoder]: Encoder[StvCandidate[E, C]] = Encoder.forProduct3("election", "candidate", "position")(c => (c.election, c.candidate, c.position))
  implicit def decoder[E, C](implicit
                             electionDecoder: Decoder[E],
                             candidateDecoder: Decoder[C],
                             candidatePositionDecoder: Decoder[CandidatePosition[E]],
                            ): Decoder[StvCandidate[E, C]] = Decoder.forProduct3("election", "candidate", "position")(StvCandidate.apply)

}
