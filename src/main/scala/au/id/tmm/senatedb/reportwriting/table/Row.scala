package au.id.tmm.senatedb.reportwriting.table

sealed trait Row[+A]

object Row {

  final case class DataRow[+A](key: A, count: Double, denominator: Double, fraction: Double) extends Row[A]

  case object TotalsRow extends Row[Nothing]

}