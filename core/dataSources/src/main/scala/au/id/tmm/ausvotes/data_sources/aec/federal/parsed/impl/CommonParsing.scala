package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.utilities.geo.australia.State

object CommonParsing {

  def parseState(rawState: String): Either[BadState, State] =
    State.fromAbbreviation(rawState.trim)
      .toRight(BadState(rawState))

  final case class BadState(rawState: String) extends ExceptionCaseClass

}
