package au.id.tmm.ausvotes.data_sources.aec.federal.extras

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState, SenateGroup, SenateHtv}

trait FetchSenateHtv[F[+_, +_]] {

  def fetchHtvCardsFor(election: SenateElection, groupsForElection: Set[SenateGroup]): F[FetchSenateHtv.Error, Map[SenateElectionForState, Set[SenateHtv]]]

}

object FetchSenateHtv {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  def fetchFor[F[+_, +_] : FetchSenateHtv](election: SenateElection, groupsForElection: Set[SenateGroup]): F[FetchSenateHtv.Error, Map[SenateElectionForState, Set[SenateHtv]]] =
    implicitly[FetchSenateHtv[F]].fetchHtvCardsFor(election, groupsForElection)

}
