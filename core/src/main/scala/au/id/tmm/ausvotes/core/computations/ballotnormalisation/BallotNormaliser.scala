package au.id.tmm.ausvotes.core.computations.ballotnormalisation

import au.id.tmm.ausvotes.core.model.computation.NormalisedBallot
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.countstv.normalisation.Preference

class BallotNormaliser private (election: SenateElectionForState,
                                candidates: Set[SenateCandidate],
                                minPreferencesAtl: Int = 1,
                                minPreferencesBtl: Int = 6) {

  private val relevantCandidates = candidates.toStream
    .filter(_.election == election)

  private val candidatesPerGroup: Map[SenateBallotGroup, Vector[SenateCandidate]] =
    relevantCandidates
      .sorted
      .toVector
      .groupBy(_.position.group)

  def normalise(ballot: SenateBallot): NormalisedBallot = {
    val (atlGroupOrder, atlCandidateOrder, atlFormalPrefCount) = normaliseAtl(ballot.groupPreferences)
    val (btlCandidateOrder, btlFormalPrefCount) = normaliseBtl(ballot.candidatePreferences)

    val canonicalCandidateOrder = if (btlCandidateOrder.nonEmpty) {
      btlCandidateOrder
    } else {
      atlCandidateOrder
    }

    NormalisedBallot(atlGroupOrder, atlCandidateOrder, atlFormalPrefCount, btlCandidateOrder, btlFormalPrefCount, canonicalCandidateOrder)
  }

  private def normaliseAtl(atlPreferences: AtlPreferences): (Vector[SenateGroup], Vector[SenateCandidate], Int) = {
    val groupsInPreferenceOrder = generalNormalise(atlPreferences, minPreferencesAtl)

    val formalPreferenceCount = groupsInPreferenceOrder.size

    val candidateOrder = distributeToCandidatePositions(groupsInPreferenceOrder)

    (groupsInPreferenceOrder, candidateOrder, formalPreferenceCount)
  }

  private def distributeToCandidatePositions(groupsInPreferenceOrder: Vector[SenateGroup]): Vector[SenateCandidate] =
    groupsInPreferenceOrder.flatMap(candidatesPerGroup)

  private def normaliseBtl(btlPreferences: Map[SenateCandidate, Preference]): (Vector[SenateCandidate], Int) = {
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
  def apply(election: SenateElectionForState, candidates: Set[SenateCandidate]): BallotNormaliser =
    new BallotNormaliser(election, candidates)
}
