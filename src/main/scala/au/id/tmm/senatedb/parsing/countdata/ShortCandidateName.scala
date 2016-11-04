package au.id.tmm.senatedb.parsing.countdata

import au.id.tmm.senatedb.model.parsing.Candidate

// TODO integrate with au.id.tmm.senatedb.model.parsing.Name in some way
private[countdata] final case class ShortCandidateName(surname: String, initial: Char)

private[countdata] object ShortCandidateName {
  def apply(surname: String, initial: String): ShortCandidateName = ShortCandidateName(surname, initial.charAt(0))

  def fromGivenAndSurname(givenName: String, surname: String): ShortCandidateName =
    ShortCandidateName(surname, givenName.charAt(0))

  def fromCandidate(candidate: Candidate): ShortCandidateName =
    fromGivenAndSurname(candidate.name.givenNames, candidate.name.surname)
}