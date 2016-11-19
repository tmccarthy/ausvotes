package au.id.tmm.senatedb.model.parsing

final case class Name(givenNames: String, surname: String) {

  def equalsIgnoreCase(that: Name): Boolean = (this.givenNames equalsIgnoreCase that.givenNames) &&
    (this.surname equalsIgnoreCase that.surname)
}

object Name {
  def parsedFrom(commaSeparatedName: String): Name = {
    val commaIndex = commaSeparatedName.indexOf(',')

    val surname = commaSeparatedName.substring(0, commaIndex).trim
    val givenNames = commaSeparatedName.substring(commaIndex + 1).trim

    Name(givenNames, surname)
  }
}
