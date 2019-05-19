package au.id.tmm.ausvotes.data_sources.aec.federal.extras.htv

import au.id.tmm.ausvotes.data_sources.aec.federal.extras.FetchSenateHtv
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState, SenateGroup, SenateHtv}
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME._

final class FetchSenateHtvFromHardcoded[F[+_, +_] : BME] private () extends FetchSenateHtv[F] {

  override def fetchHtvCardsFor(election: SenateElection, groupsForElection: Set[SenateGroup]): F[Nothing, Map[SenateElectionForState, Set[SenateHtv]]] =
    try {
      val htvsForElection = HowToVoteCardGeneration.from(election, groupsForElection)

      pure(htvsForElection.groupBy(_.election))
    } catch {
      case e: IllegalArgumentException => pure(Map.empty)
    }

}

object FetchSenateHtvFromHardcoded {

  def apply[F[+_, +_] : BME]: FetchSenateHtvFromHardcoded[F] = new FetchSenateHtvFromHardcoded()

}
