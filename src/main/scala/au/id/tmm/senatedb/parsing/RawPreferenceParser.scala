package au.id.tmm.senatedb.parsing

import au.id.tmm.senatedb.model.parsing.Ballot.{AtlPreferences, BtlPreferences}
import au.id.tmm.senatedb.model.parsing.{BallotGroup, CandidatePosition, Group, Preference}
import au.id.tmm.senatedb.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.utilities.geo.australia.State

class RawPreferenceParser private (election: SenateElection, state: State, groupsAndCandidates: GroupsAndCandidates) {

  private lazy val relevantGroups = groupsAndCandidates.groups
    .filter(g => g.election == election && g.state == state)

  private lazy val relevantCandidates = groupsAndCandidates.candidates
    .filter(c => c.election == election && c.state == state)

  private lazy val numGroupsAtl = relevantGroups.size

  private lazy val groupsInOrder: Vector[Group] = relevantGroups
    .toStream
    .sorted(BallotGroup.ordering)
    .toVector

  private lazy val indexBtlToCandidatePos: Map[Int, CandidatePosition] = {
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
      .map(Preference.fromRawValue)
      .toVector
      .splitAt(numGroupsAtl)

    val atlPreferences = atlPreferencesFrom(groupPreferencesArray)

    val btlPreferences = btlPreferencesFrom(candidatePreferencesArray)

    (atlPreferences, btlPreferences)
  }

  private def atlPreferencesFrom(groupPreferences: Vector[Option[Preference]]): AtlPreferences = {
    assert(numGroupsAtl == groupPreferences.size)

    (groupsInOrder.toStream zip groupPreferences)
      .filter { case (_, preference) => preference.isDefined }
      .map { case (group, preferenceOption) => group -> preferenceOption.get }
      .toMap
  }

  private def btlPreferencesFrom(candidatePreferences: Vector[Option[Preference]]): BtlPreferences = {
    candidatePreferences
      .toStream
      .zipWithIndex
      .filter { case (preference, index) => preference.isDefined }
      .map {
        case (preferenceOption, btlIndex) => indexBtlToCandidatePos(btlIndex) -> preferenceOption.get
      }
      .toMap
  }
}

object RawPreferenceParser {
  def apply(election: SenateElection,
            state: State,
            groupsAndCandidates: GroupsAndCandidates): RawPreferenceParser =
    new RawPreferenceParser(election, state, groupsAndCandidates)
}