package au.id.tmm.ausvotes.core.computations.donkeyvotes

import au.id.tmm.ausvotes.model.federal.senate.{AtlPreferences, SenateBallot, SenateBallotGroup}
import au.id.tmm.countstv.normalisation.Preference

object DonkeyVoteDetector {
  val threshold: Int = 4

  def isDonkeyVote(ballot: SenateBallot): Boolean = {
    ballot.groupPreferences.size >= threshold &&
      ballot.candidatePreferences.isEmpty &&
      atlPrefsAreDonkey(ballot.groupPreferences)
  }

  private def atlPrefsAreDonkey(atlPreferences: AtlPreferences): Boolean = {
    val sortedByGroup = atlPreferences.toList.sortBy { case (group, _) => group: SenateBallotGroup }

    sortedByGroup.zipWithIndex
      .map {
        case ((group, Preference.Numbered(preference)), order) => preference == (group.code.index + 1)
        case _ => false
      }
      .reduceOption(_ && _)
      .getOrElse(false)
  }
}
