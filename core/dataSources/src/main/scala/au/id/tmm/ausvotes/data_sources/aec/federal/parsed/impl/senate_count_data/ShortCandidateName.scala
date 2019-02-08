package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data

import au.id.tmm.ausvotes.model.federal.senate.SenateCandidate

final case class ShortCandidateName(surname: String, initial: Option[Char])

object ShortCandidateName {
  def apply(surname: String, initial: Char): ShortCandidateName = ShortCandidateName(surname, Some(initial))

  def apply(surname: String, initial: String): ShortCandidateName = ShortCandidateName(surname, if (initial.isEmpty) None else Some(initial.charAt(0)))

  def fromGivenAndSurname(givenName: String, surname: String): ShortCandidateName =
    ShortCandidateName(surname, givenName)

  def fromCandidate(candidate: SenateCandidate): ShortCandidateName =
    fromGivenAndSurname(candidate.candidateDetails.name.givenNames, candidate.candidateDetails.name.surname)
}
