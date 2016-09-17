package au.id.tmm.senatedb.model

trait Preferenceable {
  def preference: Option[Int]
  def mark: Option[Char]

  def parsedPreference: Preference = Preference.fromOneOf(preference, mark)
  def hasPreference: Boolean = preference.isDefined || mark.isDefined
}
