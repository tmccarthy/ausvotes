package au.id.tmm.ausvotes.core.computations.ballotnormalisation

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.computation.NormalisedBallot
import au.id.tmm.ausvotes.core.model.parsing.Ballot.AtlPreferences
import au.id.tmm.ausvotes.core.model.parsing._
import au.id.tmm.utilities.geo.australia.State

class BallotNormaliser private (election: SenateElection,
                                state: State,
                                candidates: Set[Candidate],
                                minPreferencesAtl: Int = 1,
                                minPreferencesBtl: Int = 6) {

  private type NormalisedBallotWithNumFormalPreferences = (Option[NormalisedBallot], Int)

  private val relevantCandidates = candidates.toStream
    .filter(_.election == election)
    .filter(_.state == state)

  private val positionsPerGroup: Map[BallotGroup, Vector[CandidatePosition]] =
    relevantCandidates
      .map(_.btlPosition)
      .sorted
      .toVector
      .groupBy(_.group)

  def normalise(ballot: Ballot): NormalisedBallot = {
    val (atlGroupOrder, atlCandidateOrder, atlFormalPrefCount) = normaliseAtl(ballot.atlPreferences)
    val (btlCandidateOrder, btlFormalPrefCount) = normaliseBtl(ballot.btlPreferences)

    val canonicalCandidateOrder = if (btlCandidateOrder.nonEmpty) {
      btlCandidateOrder
    } else {
      atlCandidateOrder
    }

    NormalisedBallot(atlGroupOrder, atlCandidateOrder, atlFormalPrefCount, btlCandidateOrder, btlFormalPrefCount, canonicalCandidateOrder)
  }

  private def normaliseAtl(atlPreferences: AtlPreferences): (Vector[Group], Vector[CandidatePosition], Int) = {
    val groupsInPreferenceOrder = generalNormalise(atlPreferences, minPreferencesAtl)

    val formalPreferenceCount = groupsInPreferenceOrder.size

    val candidateOrder = distributeToCandidatePositions(groupsInPreferenceOrder)

    (groupsInPreferenceOrder, candidateOrder, formalPreferenceCount)
  }

  private def distributeToCandidatePositions(groupsInPreferenceOrder: Vector[Group]): Vector[CandidatePosition] =
    groupsInPreferenceOrder.flatMap(positionsPerGroup)

  private def normaliseBtl(btlPreferences: Map[CandidatePosition, Preference]): (Vector[CandidatePosition], Int) = {
    val candidateOrder = generalNormalise(btlPreferences, minPreferencesBtl)

    val formalPreferenceCount = candidateOrder.size

    (candidateOrder, formalPreferenceCount)
  }

  private def generalNormalise[A](preferences: Map[A, Preference], minNumPreferences: Int): Vector[A] = {
    val rowsInPreferenceOrder = orderAccordingToPreferences(preferences)

    val formalPreferences = truncateAtCountError(rowsInPreferenceOrder)

    if (formalPreferences.size < minNumPreferences) {
      Vector.empty
    } else {
      formalPreferences
    }
  }

  private def orderAccordingToPreferences[A](preferences: Map[A, Preference]): Vector[Set[A]] = {
    val returnedVector: scala.collection.mutable.Buffer[Set[A]] = Vector.fill(preferences.size)(Set.empty[A]).toBuffer

    @inline def isWithinValidPreferencesRange(prefAsNumber: Int) = prefAsNumber <= preferences.size
    @inline def indexForPreference(prefAsNumber: Int) = prefAsNumber - 1

    for ((x, preference) <- preferences) {
      val preferenceAsNumber = preferenceToNumber(preference)

      if (isWithinValidPreferencesRange(preferenceAsNumber)) {
        val index = indexForPreference(preferenceAsNumber)
        returnedVector.update(index, returnedVector(index) + x)
      }
    }

    returnedVector.toVector
  }

  private def preferenceToNumber(preference: Preference): Int = {
    preference match {
      case Preference.Numbered(number) => number
      case Preference.Tick | Preference.Cross => 1
    }
  }

  private def truncateAtCountError[A](rowsInPreferenceOrder: Vector[Set[A]]): Vector[A] = {
    // As long as we have only one row with each preference, we haven't encountered a count error
    rowsInPreferenceOrder
      .toStream
      .takeWhile(_.size == 1)
      .map(_.head)
      .toVector
  }
}

object BallotNormaliser {
  def apply(election: SenateElection, state: State, candidates: Set[Candidate]): BallotNormaliser =
    new BallotNormaliser(election, state, candidates)
}