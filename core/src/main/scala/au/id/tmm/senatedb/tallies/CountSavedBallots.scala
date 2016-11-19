package au.id.tmm.senatedb.tallies
import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.model.computation.NormalisedBallot
import au.id.tmm.senatedb.model.parsing.{Ballot, Preference}

object CountSavedBallots extends PredicateTallier {

  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = {
    containedCountingError(ballotWithFacts.ballot, ballotWithFacts.normalisedBallot) ||
      markedLessSquaresThanRequired(ballotWithFacts.normalisedBallot) ||
      usedMarks(ballotWithFacts.ballot)
  }

  private val marks: Set[Preference] = Set(Preference.Tick, Preference.Cross)

  private def usedMarks(ballot: Ballot): Boolean = {
    val preferences = ballot.atlPreferences.values ++ ballot.btlPreferences.values

    preferences.exists(marks.contains)
  }

  private def containedCountingError(ballot: Ballot, normalisedBallot: NormalisedBallot): Boolean = {
    atlContainsCountingError(ballot, normalisedBallot) || btlContainsCountingError(ballot, normalisedBallot)
  }

  private def atlContainsCountingError(ballot: Ballot, normalisedBallot: NormalisedBallot): Boolean =
    ballot.atlPreferences.size > normalisedBallot.atlFormalPreferenceCount

  private def btlContainsCountingError(ballot: Ballot, normalisedBallot: NormalisedBallot): Boolean =
    ballot.btlPreferences.size > normalisedBallot.btlFormalPreferenceCount

  private def markedLessSquaresThanRequired(normalisedBallot: NormalisedBallot): Boolean =
    normalisedBallot.atlFormalPreferenceCount < 6 && normalisedBallot.btlFormalPreferenceCount < 12
}
