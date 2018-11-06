package au.id.tmm.ausvotes.api.errors

final case class NotFoundException(path: String) extends ApiException
