package au.id.tmm.senatedb.model.parsing

sealed trait Preference {
}

object Preference {
  private val tickChar = '/'
  private val crossChar = '*'

  def apply(rawValue: String): Preference = {
    val trimmedRawValue = rawValue.trim

    asMissing(trimmedRawValue)
      .orElse(asNumbered(trimmedRawValue))
      .orElse(asMark(trimmedRawValue))
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

  private def asMark(trimmedRawValue: String): Option[Preference] = {
    if (trimmedRawValue.length == 1) {
      asMark(trimmedRawValue.charAt(0))
    } else {
      None
    }
  }

  private def asMark(char: Char): Option[Preference] = {
    if (char == tickChar) {
      Some(Tick)
    } else if (char == crossChar) {
      Some(Cross)
    } else {
      None
    }
  }

  final case class Numbered(preference: Int) extends Preference {
  }

  case object Tick extends Preference

  case object Cross extends Preference

  case object Missing extends Preference
}
