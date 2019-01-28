package au.id.tmm.ausvotes.core.io_actions

import au.id.tmm.ausvotes.core.model.GroupsAndCandidates
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.{SenateCountData, SenateElectionForState}

trait FetchSenateCountData[F[+_, +_]] {

  def fetchCountDataFor(
                         election: SenateElectionForState,
                         groupsAndCandidatesForSenateElectionInState: GroupsAndCandidates,
                       ): F[FetchSenateCountData.Error, SenateCountData]

}

object FetchSenateCountData {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  def fetchFor[F[+_, +_] : FetchSenateCountData](
                                                  election: SenateElectionForState,
                                                  groupsAndCandidatesForSenateElectionInState: GroupsAndCandidates,
                                                ): F[FetchSenateCountData.Error, SenateCountData] =
    implicitly[FetchSenateCountData[F]].fetchCountDataFor(election, groupsAndCandidatesForSenateElectionInState)

}
