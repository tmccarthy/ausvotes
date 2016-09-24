package au.id.tmm.senatedb.reporting

sealed trait DenominatorType

object DenominatorType {
  case object None extends DenominatorType
  case object ByTotal extends DenominatorType
  case object ByStateTotal extends DenominatorType
  case object ByDivisionTotal extends DenominatorType
}

