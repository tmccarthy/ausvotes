package au.id.tmm.ausvotes.core.parsing

import au.id.tmm.ausvotes.core.model.parsing.Ballot.{AtlPreferences, BtlPreferences}
import au.id.tmm.ausvotes.core.model.parsing.{BallotGroup, CandidatePosition, Group, Preference}
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.utilities.geo.australia.State

class RawPreferenceParser private (election: SenateElection, state: State, groupsAndCandidates: GroupsAndCandidates) {

  private val relevantGroups = groupsAndCandidates.groups
    .filter(g => g.election == election && g.state == state)

  private val relevantCandidates = groupsAndCandidates.candidates
    .filter(c => c.election == election && c.state == state)

  private val numGroupsAtl = relevantGroups.size

  private val groupsInOrder: Vector[Group] = relevantGroups
    .toStream
    .sorted(BallotGroup.ordering)
    .toVector

  private val indexBtlToCandidatePos: Map[Int, CandidatePosition] = {
    val btlCandidatesInOrder = relevantCandidates
      .toStream
      .sortBy(_.btlPosition)

    btlCandidatesInOrder.zipWithIndex
      .map {
        case (candidate, btlIndex) => btlIndex -> candidate.btlPosition
      }
      .toMap
  }

  def preferencesFrom(rawPreferencesString: String): (AtlPreferences, BtlPreferences) = {
    val (groupPreferencesArray, candidatePreferencesArray) = rawPreferencesString.split(",", -1)
      .splitAt(numGroupsAtl)

    val atlPreferences = atlPreferencesFrom(groupPreferencesArray)

    val btlPreferences = btlPreferencesFrom(candidatePreferencesArray)

    (atlPreferences, btlPreferences)
  }

  private def atlPreferencesFrom(rawGroupPreferences: Array[String]): AtlPreferences = {
    assert(numGroupsAtl == rawGroupPreferences.length)

    val returnedMapBuilder = Map.newBuilder[Group, Preference]

    for (groupIndex <- groupsInOrder.indices) {
      val groupPreferenceOption = Preference.fromRawValue(rawGroupPreferences(groupIndex))

      if (groupPreferenceOption.isDefined) {
        returnedMapBuilder += (groupsInOrder(groupIndex) -> groupPreferenceOption.get)
      }
    }

    returnedMapBuilder.result()
  }

  private def btlPreferencesFrom(candidatePreferences: Array[String]): BtlPreferences = {
    val returnedMapBuilder = Map.newBuilder[CandidatePosition, Preference]

    for (candidateIndex <- candidatePreferences.indices) {
      val candidatePreferenceOption = Preference.fromRawValue(candidatePreferences(candidateIndex))

      if (candidatePreferenceOption.isDefined) {
        returnedMapBuilder += (indexBtlToCandidatePos(candidateIndex) -> candidatePreferenceOption.get)
      }
    }

    returnedMapBuilder.result()
  }
}

object RawPreferenceParser {
  def apply(election: SenateElection,
            state: State,
            groupsAndCandidates: GroupsAndCandidates): RawPreferenceParser =
    new RawPreferenceParser(election, state, groupsAndCandidates)
}