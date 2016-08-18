package au.id.tmm.senatedb.data

sealed trait Preference {
  def asNumber: Option[Int] = None
  def asSpecialChar: Option[Char] = None
}

object Preference {
  def apply(rawValue: String): Preference = {
    val trimmedRawValue = rawValue.trim

    asMissing(trimmedRawValue)
      .orElse(asNumbered(trimmedRawValue))
      .orElse(asSpecialChar(trimmedRawValue))
      .getOrElse(throw new IllegalArgumentException(s"$rawValue is not a valid preference"))
  }

  private def asMissing(trimmedRawValue: String): Option[Preference] = {
    if (trimmedRawValue.isEmpty) {
      Some(Missing)
    } else {
      None
    }
  }

  private def asNumbered(trimmedRawValue: String): Option[Numbered] = {
    try {
      Some(Numbered(trimmedRawValue.toInt))
    } catch {
      case e: NumberFormatException => None
    }
  }

  private def asSpecialChar(trimmedRawValue: String): Option[SpecialChar] = {
    if (trimmedRawValue.length == 1) {
      Some(SpecialChar(trimmedRawValue.charAt(0)))
    } else {
      None
    }
  }
}

final case class Numbered(preference: Int) extends Preference {
  override def asNumber: Option[Int] = Some(preference)
}

final case class SpecialChar(char: Char) extends Preference {
  override def asSpecialChar: Option[Char] = Some(char)
}

case object Missing extends Preference
