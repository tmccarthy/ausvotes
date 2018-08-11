package au.id.tmm.ausvotes.shared.aws

final case class S3ObjectKey(elements: List[String]) {
  def /(key: String): S3ObjectKey = S3ObjectKey(elements :+ key)

  def asString: String = elements.mkString("/")
}

object S3ObjectKey {
  def apply(key: String): S3ObjectKey = S3ObjectKey(List(key))

  def apply(keyParts: String*): S3ObjectKey = S3ObjectKey(keyParts.toList)
}

