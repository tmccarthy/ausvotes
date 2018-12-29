package au.id.tmm.ausvotes.core.parsing.countdata

import au.id.tmm.ausvotes.model.federal.senate.SenateCandidate

// TODO integrate with au.id.tmm.ausvotes.model.parsing.Name in some way
private[countdata] final case class ShortCandidateName(surname: String, initial: Char)

private[countdata] object ShortCandidateName {
  def apply(surname: String, initial: String): ShortCandidateName = ShortCandidateName(surname, initial.charAt(0))

  def fromGivenAndSurname(givenName: String, surname: String): ShortCandidateName =
    ShortCandidateName(surname, givenName.charAt(0))

  def fromCandidate(candidate: SenateCandidate): ShortCandidateName =
    fromGivenAndSurname(candidate.candidate.name.givenNames, candidate.candidate.name.surname)
}
