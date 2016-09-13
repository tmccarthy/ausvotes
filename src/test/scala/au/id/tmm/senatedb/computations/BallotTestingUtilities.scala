package au.id.tmm.senatedb.computations

import au.id.tmm.senatedb.data.database.model.{AtlPreferencesRow, BtlPreferencesRow}
import au.id.tmm.senatedb.model.{CandidatePosition, NormalisedBallot, Preference}

object BallotTestingUtilities {
  private val testBallotId = "BALLOTID"

  def orderedAtlPreferences(groupsInOrder: String*): Set[AtlPreferencesRow] = {
    val preferencesPerGroup = groupsInOrder.zipWithIndex
      .map { case (group, index) => (group, (index + 1).toString) }

    atlPreferences(preferencesPerGroup: _*)
  }

  def atlPreferences(prefPerGroup: (String, String)*): Set[AtlPreferencesRow] = {
    prefPerGroup.map {
      case (group, rawPref) => (group, Preference(rawPref))
    }.map {
      case (group, preference) => AtlPreferencesRow(testBallotId, group, preference.asNumber, preference.asChar)
    }.toSet
  }

  def orderedBtlPreferences(candidatesInOrder: String*): Set[BtlPreferencesRow] = {
    val preferencesPerCandidate = candidatesInOrder.zipWithIndex
      .map { case (candidate, index) => (candidate, (index + 1).toString) }

    btlPreferences(preferencesPerCandidate: _*)
  }

  def btlPreferences(prefPerCandidate: (String, String)*): Set[BtlPreferencesRow] = {
    prefPerCandidate.map {
      case (posCode, rawPref) => (codeToCandidatePosition(posCode), Preference(rawPref))
    }.map {
      case (position, preference) => BtlPreferencesRow(testBallotId, position.group, position.positionInGroup, preference.asNumber, preference.asChar)
    }.toSet
  }

  private val candidatePositionCodePattern = "([A-Z]+)(\\d+)".r

  private def codeToCandidatePosition(positionCode: String) = positionCode match {
    case candidatePositionCodePattern(group, position) => CandidatePosition(group, position.toInt)
  }

  def normalisedBallot(candidatesInOrder: String*): NormalisedBallot = {
    val positionsInOrder = candidatesInOrder.map(codeToCandidatePosition)

    NormalisedBallot(positionsInOrder.toVector)
  }
}
