package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences

private[this] final case class ShortCandidateName(surname: String, initial: Char)

private[this] object ShortCandidateName {
  def apply(surname: String, initial: String): ShortCandidateName = ShortCandidateName(surname, initial.charAt(0))

  def fromGivenAndSurname(givenName: String, surname: String): ShortCandidateName =
    ShortCandidateName(surname, givenName.charAt(0))

  def fromCandidateCsvRow(row: DopCsvRow): ShortCandidateName = fromGivenAndSurname(row.givenName, row.surname)
}