package au.id.tmm.ausvotes.shared.recountresources.entities.actions

import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates.FetchGroupsAndCandidatesException
import au.id.tmm.ausvotes.shared.recountresources.exceptions
import au.id.tmm.utilities.geo.australia.State

trait FetchGroupsAndCandidates[F[+_, +_]] {

  def fetchGroupsAndCandidatesFor(election: SenateElection, state: State): F[FetchGroupsAndCandidatesException, GroupsAndCandidates]

}

object FetchGroupsAndCandidates {

  def fetchGroupsAndCandidatesFor[F[+_, +_] : FetchGroupsAndCandidates](
                                                                       election: SenateElection,
                                                                       state: State,
                                                                     ): F[FetchGroupsAndCandidatesException, GroupsAndCandidates] =
    implicitly[FetchGroupsAndCandidates[F]].fetchGroupsAndCandidatesFor(election, state)

  sealed abstract class FetchGroupsAndCandidatesException extends ExceptionCaseClass

  object FetchGroupsAndCandidatesException {
    final case class LoadGroupsJsonException(cause: Exception) extends FetchGroupsAndCandidatesException with ExceptionCaseClass.WithCause
    final case class DecodeGroupsJsonException(message: String) extends FetchGroupsAndCandidatesException
    final case class LoadCandidatesJsonException(cause: Exception) extends FetchGroupsAndCandidatesException with ExceptionCaseClass.WithCause
    final case class InvalidCandidatesJsonException(cause: exceptions.InvalidJsonException) extends FetchGroupsAndCandidatesException with ExceptionCaseClass.WithCause
    final case class DecodeCandidatesJsonException(message: String) extends FetchGroupsAndCandidatesException
  }

}
