package au.id.tmm.ausvotes.core.model.parsing

sealed trait Preference {
}

object Preference {
  private val tickChar = '/'
  private val crossChar = '*'

  def fromRawValue(rawValue: String): Option[Preference] = {
    val trimmedRawValue = rawValue.trim

    if (trimmedRawValue.isEmpty) {
      None
    } else {
      asNumbered(trimmedRawValue)
        .orElse(asMark(trimmedRawValue))
        .orElse(throw new IllegalArgumentException(s"$rawValue is not a valid preference"))
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
}
