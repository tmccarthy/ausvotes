package au.id.tmm.senatedb.reporting.reports

import au.id.tmm.senatedb.model.parsing.{Ballot, Preference}

private[reports] object PreferenceDetectionUtilities {
  def containsPreference(ballot: Ballot, preference: Preference): Boolean = {
    containsPreference(ballot.atlPreferences, preference) || containsPreference(ballot.btlPreferences, preference)
  }

  def containsPreference(preferences: Map[_, Preference], preference: Preference): Boolean = {
    preferences.values.exists(_ == preference)
  }
}
