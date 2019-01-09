package au.id.tmm.ausvotes.core.parsing

import au.id.tmm.ausvotes.core.model.GroupsAndCandidates
import au.id.tmm.ausvotes.core.parsing.RawPreferenceParser.preferenceFromRawValue
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.stv.BallotGroup
import au.id.tmm.countstv.normalisation.Preference
import au.id.tmm.countstv.normalisation.Preference.{Cross, Numbered, Tick}

class RawPreferenceParser private (election: SenateElectionForState, groupsAndCandidates: GroupsAndCandidates) {

  private val relevantGroups = groupsAndCandidates.groups
    .filter(g => g.election == election)

  private val relevantCandidates = groupsAndCandidates.candidates
    .filter(c => c.election == election)

  private val numGroupsAtl = relevantGroups.size

  private val groupsInOrder: Vector[SenateGroup] = relevantGroups
    .toStream
    .sorted(BallotGroup.ordering(implicitly[Ordering[SenateElectionForState]]))
    .toVector

  private val indexBtlToCandidate: Map[Int, SenateCandidate] = {
    val btlCandidatesInOrder = relevantCandidates.toList.sorted

    btlCandidatesInOrder.zipWithIndex
      .map {
        case (candidate, btlIndex) => btlIndex -> candidate
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

    val returnedMapBuilder = Map.newBuilder[SenateGroup, Preference]

    for (groupIndex <- groupsInOrder.indices) {
      val groupPreferenceOption = preferenceFromRawValue(rawGroupPreferences(groupIndex))

      if (groupPreferenceOption.isDefined) {
        returnedMapBuilder += (groupsInOrder(groupIndex) -> groupPreferenceOption.get)
      }
    }

    returnedMapBuilder.result()
  }

  private def btlPreferencesFrom(candidatePreferences: Array[String]): BtlPreferences = {
    val returnedMapBuilder = Map.newBuilder[SenateCandidate, Preference]

    for (candidateIndex <- candidatePreferences.indices) {
      val candidatePreferenceOption = preferenceFromRawValue(candidatePreferences(candidateIndex))

      if (candidatePreferenceOption.isDefined) {
        returnedMapBuilder += (indexBtlToCandidate(candidateIndex) -> candidatePreferenceOption.get)
      }
    }

    returnedMapBuilder.result()
  }
}

object RawPreferenceParser {

  private val tickChar = '/'
  private val crossChar = '*'

  def apply(
             election: SenateElectionForState,
             groupsAndCandidates: GroupsAndCandidates,
           ): RawPreferenceParser =
    new RawPreferenceParser(election, groupsAndCandidates)

  private[parsing] def preferenceFromRawValue(rawValue: String): Option[Preference] = {
    val trimmedRawValue = rawValue.trim

    if (trimmedRawValue.isEmpty) {
      None
    } else {
      asNumbered(trimmedRawValue)
        .orElse(asMark(trimmedRawValue))
        .orElse(throw new IllegalArgumentException(s"$rawValue is not a valid preference"))
    }
  }

  private def asNumbered(trimmedRawValue: String): Option[Numbered] = {
    try {
      Some(Numbered(trimmedRawValue.toInt))
    } catch {
      case e: NumberFormatException => None
    }
  }

  private def asMark(trimmedRawValue: String): Option[Preference] = {
    if (trimmedRawValue.length == 1) {
      asMark(trimmedRawValue.charAt(0))
    } else {
      None
    }
  }

  private def asMark(char: Char): Option[Preference] = {
    if (char == tickChar) {
      Some(Tick)
    } else if (char == crossChar) {
      Some(Cross)
    } else {
      None
    }
  }
}
