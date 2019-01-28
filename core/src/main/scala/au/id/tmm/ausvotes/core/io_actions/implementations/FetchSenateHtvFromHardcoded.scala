package au.id.tmm.ausvotes.core.io_actions.implementations

import au.id.tmm.ausvotes.core.io_actions.FetchSenateHtv
import au.id.tmm.ausvotes.core.parsing.HowToVoteCardGeneration
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState, SenateGroup, SenateHtv}
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError._

final class FetchSenateHtvFromHardcoded[F[+_, +_] : BifunctorMonadError] extends FetchSenateHtv[F] {

  override def fetchHtvCardsFor(election: SenateElection, groupsForElection: Set[SenateGroup]): F[Nothing, Map[SenateElectionForState, Set[SenateHtv]]] =
    try {
      val htvsForElection = HowToVoteCardGeneration.from(election, groupsForElection)

      pure(htvsForElection.groupBy(_.election))
    } catch {
      case e: IllegalArgumentException => pure(Map.empty)
    }

}
