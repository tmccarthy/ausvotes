package au.id.tmm.ausvotes.shared.recountresources.exceptions

import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass

final case class InvalidJsonException(message: String) extends ExceptionCaseClass
