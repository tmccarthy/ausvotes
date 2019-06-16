package au.id.tmm.ausvotes.data_sources.common

import org.apache.commons.lang3.StringUtils

object CsvParsing {

  def parsePossibleBoolean(string: String): Option[Boolean] = if (string.isEmpty) None else Some(string.toBoolean)
  def parsePossibleInt(string: String): Option[Int] = if (string.isEmpty) None else Some(string.toInt)
  def parsePossibleDouble(string: String): Option[Double] = if (string.isEmpty) None else Some(string.toDouble)
  def parsePossibleString(string: String): Option[String] = if (string.isEmpty) None else Some(string)

  def noneIfBlank(string: String): Option[String] = if (StringUtils.isBlank(string)) None else Some(string)

}
