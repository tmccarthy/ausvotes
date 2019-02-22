package au.id.tmm.ausvotes.core.computations.donkeyvotes

import au.id.tmm.ausvotes.model.stv.{Ballot, BallotGroup, Group}
import au.id.tmm.countstv.normalisation.Preference

object DonkeyVoteDetector {
  val threshold: Int = 4

  def isDonkeyVote[E : Ordering, J, I](ballot: Ballot[E, J, I]): Boolean = {
    ballot.groupPreferences.size >= threshold &&
      ballot.candidatePreferences.isEmpty &&
      atlPrefsAreDonkey(ballot.groupPreferences)
  }

  private def atlPrefsAreDonkey[E : Ordering](atlPreferences: Map[Group[E], Preference]): Boolean = {
    val sortedByGroup = atlPreferences.toList.sortBy { case (group, _) => group: BallotGroup[E] }

    sortedByGroup.zipWithIndex
      .map {
        case ((group, Preference.Numbered(preference)), order) => preference == (group.code.index + 1)
        case _ => false
      }
      .reduceOption(_ && _)
      .getOrElse(false)
  }
}
