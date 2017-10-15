package au.id.tmm.ausvotes.core.model.computation

sealed trait SavingsProvision {
}

object SavingsProvision {
  sealed trait UsedMark extends SavingsProvision

  case object UsedTick extends UsedMark
  case object UsedCross extends UsedMark

  sealed trait CountingError extends SavingsProvision

  case object CountingErrorAtl extends CountingError
  case object CountingErrorBtl extends CountingError

  sealed trait InsufficientPreferences extends SavingsProvision

  case object InsufficientPreferencesAtl extends InsufficientPreferences
  case object InsufficientPreferencesBtl extends InsufficientPreferences
}