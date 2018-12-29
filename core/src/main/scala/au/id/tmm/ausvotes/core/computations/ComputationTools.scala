package au.id.tmm.ausvotes.core.computations

import au.id.tmm.ausvotes.core.computations.ComputationTools.{ElectionLevelTools, StateLevelTools}
import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.computations.howtovote.MatchingHowToVoteCalculator

final case class ComputationTools(electionLevel: ElectionLevelTools, stateLevel: StateLevelTools)

object ComputationTools {
  final case class ElectionLevelTools(matchingHowToVoteCalculator: MatchingHowToVoteCalculator)

  final case class StateLevelTools(ballotNormaliser: BallotNormaliser)
}
