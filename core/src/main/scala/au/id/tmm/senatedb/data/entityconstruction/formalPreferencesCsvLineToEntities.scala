package au.id.tmm.senatedb.data.entityconstruction

import au.id.tmm.senatedb.data.database._
import au.id.tmm.senatedb.data.{Missing, Preference, computeBallotId}
import au.id.tmm.senatedb.model.{SenateElection, State}

import scala.collection.immutable.ListMap
import scala.collection.mutable
import scala.util.Try

// TODO ListMap -> OrderedMap
object formalPreferencesCsvLineToEntities
  extends ((SenateElection, State, ListMap[String, Int], Seq[String]) => Try[BallotWithPreferences]) {

  override def apply(election: SenateElection,
                     state: State,
                     numCandidatesPerGroup: ListMap[String, Int],
                     row: Seq[String]): Try[BallotWithPreferences] = Try {

    val ballot = ballotRowOf(election, state, row)

    val preferenceString = row(5)
    val (atlPreferences, btlPreferences) = preferencesRowsOf(ballot.ballotId, numCandidatesPerGroup, preferenceString)

    BallotWithPreferences(ballot, atlPreferences, btlPreferences)
  }

  private def ballotRowOf(election: SenateElection, state: State, row: Seq[String]): BallotRow = {
    val electorate = row(0)
    val voteCollectionPointId = row(2).toInt
    val batchNo = row(3).toInt
    val paperNo = row(4).toInt

    val ballotId = computeBallotId(election.aecID, state.shortName, voteCollectionPointId, batchNo, paperNo)

    BallotRow(
      ballotId,
      election.aecID,
      state.shortName,
      electorate,
      voteCollectionPointId,
      batchNo,
      paperNo
    )
  }

  // TODO check that the number of candidates/groups is consistent with the size of the preferences array
  private def preferencesRowsOf(ballotId: String,
                                numCandidatesPerGroup: ListMap[String, Int],
                                preferencesString: String): (Set[AtlPreferencesRow], Set[BtlPreferencesRow]) = {
    val (groupPreferencesArray, candidatePreferencesArray) = preferencesString.split(",", -1)
      .map(parsePreferenceFromString)
      .toVector
      .splitAt(numCandidatesPerGroup.keySet.size)

    val atlPreferences = atlPreferencesFrom(ballotId, numCandidatesPerGroup, groupPreferencesArray)

    val btlPreferences = btlPreferencesFrom(ballotId, numCandidatesPerGroup, candidatePreferencesArray)

    (atlPreferences, btlPreferences)
  }

  private def parsePreferenceFromString(preferenceAsString: String): Preference = {
    Preference(preferenceAsString)
  }

  private def atlPreferencesFrom(ballotId: String,
                                 numCandidatesPerGroup: ListMap[String, Int],
                                 groupPreferences: Vector[Preference]) = {
    assert(numCandidatesPerGroup.keySet.size == groupPreferences.size)

    (numCandidatesPerGroup.keys zip groupPreferences)
      .filter { case (_, preference) => preference != Missing }
      .map { case (group, preference) => AtlPreferencesRow(ballotId, group, preference.asNumber, preference.asSpecialChar) }
      .toSet
  }

  // TODO this needs refactoring pretty badly
  private def btlPreferencesFrom(ballotId: String,
                                 numCandidatesPerGroup: ListMap[String, Int],
                                 candidatePreferences: Vector[Preference]) = {
    type GroupAndGroupPosition = (String, Int)

    def accumulateIntoCandidateIndexMap(groupPositionPerCandidateIndex: mutable.LinkedHashMap[Int, GroupAndGroupPosition],
                                        group: String,
                                        numCandidatesInThisGroup: Int): mutable.LinkedHashMap[Int, GroupAndGroupPosition] = {
      val largestExistingIndex = if (groupPositionPerCandidateIndex.isEmpty) -1 else groupPositionPerCandidateIndex.keySet.max

      val nextCandidateIndex = largestExistingIndex + 1

      val positionsForThisGroup = Range(0, numCandidatesInThisGroup)

      positionsForThisGroup.foreach(positionInGroup => groupPositionPerCandidateIndex.put(nextCandidateIndex + positionInGroup, (group, positionInGroup)))

      groupPositionPerCandidateIndex
    }

    val groupAndGroupPositionPerCandidateIndex = numCandidatesPerGroup
      .toStream
      .foldLeft(mutable.LinkedHashMap[Int, GroupAndGroupPosition]()) {
      case (mapSoFar, (group, numCandidatesInGroup)) => accumulateIntoCandidateIndexMap(mapSoFar, group, numCandidatesInGroup)
    }

    candidatePreferences.zipWithIndex
      .filter {
        case (preference, _) => preference != Missing
      }
      .map {
        case (preference, candidateIndex) => {
          val groupAndGroupPosition = groupAndGroupPositionPerCandidateIndex(candidateIndex)

          BtlPreferencesRow(ballotId, groupAndGroupPosition._1, groupAndGroupPosition._2, preference.asNumber, preference.asSpecialChar)
        }
      }
      .toSet
  }
}
