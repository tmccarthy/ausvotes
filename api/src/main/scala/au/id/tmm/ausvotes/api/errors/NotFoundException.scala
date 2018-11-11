package au.id.tmm.ausvotes.api.errors

import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass

final case class NotFoundException(path: String) extends ExceptionCaseClass
