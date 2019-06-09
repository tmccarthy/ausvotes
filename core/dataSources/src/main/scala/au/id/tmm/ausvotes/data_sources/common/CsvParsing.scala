package au.id.tmm.ausvotes.data_sources.common

object CsvParsing {

  def parsePossibleBoolean(string: String): Option[Boolean] = if (string.isEmpty) None else Some(string.toBoolean)
  def parsePossibleInt(string: String): Option[Int] = if (string.isEmpty) None else Some(string.toInt)
  def parsePossibleDouble(string: String): Option[Double] = if (string.isEmpty) None else Some(string.toDouble)
  def parsePossibleString(string: String): Option[String] = if (string.isEmpty) None else Some(string)

}
