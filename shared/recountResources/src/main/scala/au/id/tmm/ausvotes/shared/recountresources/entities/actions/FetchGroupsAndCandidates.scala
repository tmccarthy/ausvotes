package au.id.tmm.ausvotes.shared.recountresources.entities.actions

import au.id.tmm.ausvotes.model.federal.senate.{SenateGroupsAndCandidates, SenateElectionForState}
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates.FetchGroupsAndCandidatesException
import au.id.tmm.ausvotes.shared.recountresources.exceptions

trait FetchGroupsAndCandidates[F[+_, +_]] {

  def fetchGroupsAndCandidatesFor(election: SenateElectionForState): F[FetchGroupsAndCandidatesException, SenateGroupsAndCandidates]

}

object FetchGroupsAndCandidates {

  def fetchGroupsAndCandidatesFor[F[+_, +_] : FetchGroupsAndCandidates](
                                                                         election: SenateElectionForState,
                                                                       ): F[FetchGroupsAndCandidatesException, SenateGroupsAndCandidates] =
    implicitly[FetchGroupsAndCandidates[F]].fetchGroupsAndCandidatesFor(election)

  sealed abstract class FetchGroupsAndCandidatesException extends ExceptionCaseClass

  object FetchGroupsAndCandidatesException {
    final case class LoadGroupsJsonException(cause: Exception) extends FetchGroupsAndCandidatesException with ExceptionCaseClass.WithCause
    final case class DecodeGroupsJsonException(message: String) extends FetchGroupsAndCandidatesException
    final case class LoadCandidatesJsonException(cause: Exception) extends FetchGroupsAndCandidatesException with ExceptionCaseClass.WithCause
    final case class InvalidCandidatesJsonException(cause: exceptions.InvalidJsonException) extends FetchGroupsAndCandidatesException with ExceptionCaseClass.WithCause
    final case class DecodeCandidatesJsonException(message: String) extends FetchGroupsAndCandidatesException
  }

}
