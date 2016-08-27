package au.id.tmm.senatedb.commandline

sealed trait Verb {

}

object Verb {
  case object UNSPECIFIED extends Verb
  case object LOAD extends Verb
  case object RELOAD extends Verb

  def fromString(name: String): Option[Verb] = name.toUpperCase.trim match {
    case "LOAD" => Some(LOAD)
    case "RELOAD" => Some(RELOAD)
    case "" => Some(UNSPECIFIED)
    case _ => None
  }
}