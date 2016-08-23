package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.database.{AtlPreferencesRow, BallotRow, BtlPreferencesRow}

private[data] final case class BallotWithPreferences(ballot: BallotRow,
                                                     atlPreferences: Set[AtlPreferencesRow],
                                                     btlPreferences: Set[BtlPreferencesRow]) {

  def unapply = BallotWithPreferences.unapply(this).get
}
