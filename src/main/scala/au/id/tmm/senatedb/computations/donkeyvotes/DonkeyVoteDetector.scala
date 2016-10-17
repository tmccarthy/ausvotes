package au.id.tmm.senatedb.computations.donkeyvotes

import au.id.tmm.senatedb.model.parsing.Ballot.AtlPreferences
import au.id.tmm.senatedb.model.parsing.{Ballot, BallotGroup, Preference}

object DonkeyVoteDetector {
  val threshold: Int = 4

  def isDonkeyVote(ballot: Ballot): Boolean = {
    ballot.atlPreferences.size >= threshold &&
      ballot.btlPreferences.isEmpty &&
      atlPrefsAreDonkey(ballot.atlPreferences)
  }

  private def atlPrefsAreDonkey(atlPreferences: AtlPreferences): Boolean = {
    val sortedByGroup = atlPreferences.toStream.sortBy { case (group, _) => group.asInstanceOf[BallotGroup] }

    sortedByGroup.zipWithIndex
      .map {
        case ((_, Preference.Numbered(preference)), order) => preference == (order + 1)
      }
      .reduceOption(_ && _)
      .getOrElse(false)
  }
}