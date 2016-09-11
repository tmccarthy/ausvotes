package au.id.tmm.senatedb.model

sealed trait Preference {
  def asNumber: Option[Int] = None
  def asChar: Option[Char] = None
}

object Preference {
  private val tickChar = '/'
  private val crossChar = '*'

  def fromOneOf(preference: Option[Int],
                mark: Option[Char]): Preference = {
    if (preference.isDefined && mark.isEmpty) {
      Numbered(preference.get)
    } else if (preference.isEmpty && mark.isDefined) {
      asMark(mark.get)
        .getOrElse(throw new IllegalArgumentException(s"Invalid mark character '${mark.get}'"))
    } else if (preference.isEmpty && mark.isEmpty) {
      Missing
    } else {
      throw new IllegalArgumentException("Can only construct a preference from *either* a preference *or* a mark")
    }
  }

  def spread(preference: Preference): (Option[Int], Option[Char]) = {
    preference match {
      case Numbered(number) => (Some(number), None)
      case Tick => (None, Some(tickChar))
      case Cross => (None, Some(crossChar))
      case Missing => (None, None)
    }
  }

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
    override val asNumber = Some(preference)
  }

  case object Tick extends Preference {
    override val asChar = Some(tickChar)
  }

  case object Cross extends Preference {
    override val asChar = Some(crossChar)
  }

  case object Missing extends Preference
}
