package au.id.tmm.ausvotes.model.federal

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.StateCodec.codec
import au.id.tmm.utilities.geo.australia.State
import io.circe.{Decoder, DecodingFailure, Encoder}

final case class SenateElectionForState private (
                                                  election: SenateElection,
                                                  state: State,
                                                )

object SenateElectionForState {

  def apply(election: SenateElection, state: State): Either[NoElectionForState, SenateElectionForState] =
    if (election.states contains state) {
      Right(new SenateElectionForState(election, state))
    } else {
      Left(NoElectionForState(election, state))
    }

  implicit val encoder: Encoder[SenateElectionForState] = Encoder.forProduct2("election", "state")(n => (n.election, n.state))
  implicit val decoder: Decoder[SenateElectionForState] = cursor =>
    for {
      election <- cursor.get[SenateElection]("election")
      state <- cursor.get[State]("state")
      electionForState <- SenateElectionForState(election, state) match {
        case Right(value) => Right(value)
        case Left(NoElectionForState(election, state)) => Left(DecodingFailure(s"No election for ${state.abbreviation} at ${election.name}", cursor.history))
      }
    } yield electionForState

  final case class NoElectionForState(election: SenateElection, state: State) extends ExceptionCaseClass

}
