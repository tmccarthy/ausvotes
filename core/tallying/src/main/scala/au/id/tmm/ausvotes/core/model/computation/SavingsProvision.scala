package au.id.tmm.ausvotes.core.model.computation

sealed trait SavingsProvision {
}

object SavingsProvision {
  sealed trait UsedMark extends SavingsProvision

  case object UsedTick extends UsedMark
  case object UsedCross extends UsedMark

  case object CountingError extends SavingsProvision

  case object InsufficientPreferences extends SavingsProvision
}
