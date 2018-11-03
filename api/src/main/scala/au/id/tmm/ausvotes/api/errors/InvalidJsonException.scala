package au.id.tmm.ausvotes.api.errors

final case class InvalidJsonException(message: String) extends ApiException
