package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.formalpreferences

import au.id.tmm.senatedb.data.GroupsAndCandidates
import au.id.tmm.senatedb.data.database.model.{AtlPreferencesRow, BtlPreferencesRow}
import au.id.tmm.senatedb.model.{CandidatePosition, GroupUtils, Preference}

private[formalpreferences] class RawPreferenceParser(groupsAndCandidates: GroupsAndCandidates) {

  private lazy val numGroupsAtl = groupsAndCandidates.groups.size
  private lazy val groupsInOrder: Vector[String] = groupsAndCandidates.groups
    .toStream
    .map(_.groupId)
    .sorted(GroupUtils.groupOrdering)
    .toVector

  private lazy val indexBtlToCandidatePos: Map[Int, CandidatePosition] = {
    val btlCandidatesInOrder = groupsAndCandidates.candidates
      .toStream
      .sortBy(_.positionInGroup)

    btlCandidatesInOrder.zipWithIndex
      .map {
        case (row, btlIndex) => btlIndex -> row.position
      }
      .toMap
  }

  def preferencesFrom(ballotId: String, rawPreferencesString: String): (Set[AtlPreferencesRow], Set[BtlPreferencesRow]) = {
    val (groupPreferencesArray, candidatePreferencesArray) = rawPreferencesString.split(",", -1)
      .map(Preference(_))
      .toVector
      .splitAt(numGroupsAtl)

    val atlPreferences = atlPreferencesFrom(ballotId, groupPreferencesArray)

    val btlPreferences = btlPreferencesFrom(ballotId, candidatePreferencesArray)

    (atlPreferences, btlPreferences)
  }

  private def atlPreferencesFrom(ballotId: String,
                                 groupPreferences: Vector[Preference]): Set[AtlPreferencesRow] = {
    assert(numGroupsAtl == groupPreferences.size)

    (groupsInOrder.toStream zip groupPreferences)
      .filter { case (_, preference) => preference != Preference.Missing }
      .map { case (group, preference) => AtlPreferencesRow(ballotId, group, preference.asNumber, preference.asChar) }
      .toSet
  }

  private def btlPreferencesFrom(ballotId: String, candidatePreferences: Vector[Preference]): Set[BtlPreferencesRow] = {
    candidatePreferences
      .toStream
      .filterNot(_ == Preference.Missing)
      .zipWithIndex
      .map {
        case (preference, btlIndex) => (preference, indexBtlToCandidatePos(btlIndex))
      }
      .map {
        case (preference, candidatePosition) => BtlPreferencesRow(ballotId, candidatePosition, preference)
      }
      .toSet
  }

}
