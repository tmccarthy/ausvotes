package au.id.tmm.ausvotes.model.stv

import io.circe.{Decoder, Encoder}

final case class StvCandidate[E, C](
                                     election: E,
                                     candidateDetails: C,
                                     position: CandidatePosition[E],
                                   )

object StvCandidate {

  implicit def ordering[E : Ordering, C]: Ordering[StvCandidate[E, C]] = Ordering.by(_.position)

  implicit def encoder[E : Encoder, C : Encoder]: Encoder[StvCandidate[E, C]] = Encoder.forProduct3("election", "candidateDetails", "position")(c => (c.election, c.candidateDetails, c.position))
  implicit def decoder[E, C](implicit
                             electionDecoder: Decoder[E],
                             candidateDecoder: Decoder[C],
                             candidatePositionDecoder: Decoder[CandidatePosition[E]],
                            ): Decoder[StvCandidate[E, C]] = Decoder.forProduct3("election", "candidateDetails", "position")(StvCandidate.apply)

}
