package au.id.tmm.ausvotes.model.nsw.legco

import au.id.tmm.ausvotes.model.nsw.NswElection
import io.circe.{Decoder, Encoder}

final case class NswLegCoElection(stateElection: NswElection) extends AnyVal

object NswLegCoElection {
  implicit val ordering: Ordering[NswLegCoElection] = Ordering.by(_.stateElection)

  implicit val encoder: Encoder[NswLegCoElection] = Encoder.forProduct1("stateElection")(_.stateElection)
  implicit val decoder: Decoder[NswLegCoElection] = Decoder.forProduct1("stateElection")(NswLegCoElection.apply)
}
