package au.id.tmm.ausvotes.shared.aws.data

final case class ContentType(asString: String) extends AnyVal

object ContentType {

  val TEXT_CSV = ContentType("text/csv")
  val TEXT_HTML = ContentType("text/html")
  val IMAGE_JPEG = ContentType("image/jpeg")
  val APPLICATION_JSON = ContentType("application/json")
  val TEXT_PLAIN = ContentType("text/plain")
  val APPLICATION_XML = ContentType("application/xml")

}
