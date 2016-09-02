package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.database.model.{AtlPreferencesRow, BallotFactsRow, BallotRow, BtlPreferencesRow}

private[data] final case class BallotWithPreferences(ballot: BallotRow,
                                                     ballotFacts: BallotFactsRow,
                                                     atlPreferences: Set[AtlPreferencesRow],
                                                     btlPreferences: Set[BtlPreferencesRow]) {

  def unapply = BallotWithPreferences.unapply(this).get
}
