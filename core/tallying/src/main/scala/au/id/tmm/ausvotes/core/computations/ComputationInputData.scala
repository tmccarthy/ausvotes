package au.id.tmm.ausvotes.core.computations

import au.id.tmm.ausvotes.core.computations.ComputationInputData.{ElectionLevelData, StateLevelData}
import au.id.tmm.ausvotes.model.federal.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.model.federal.senate.{SenateCountData, SenateGroupsAndCandidates, SenateHtv}

final case class ComputationInputData(electionLevel: ElectionLevelData, stateLevel: StateLevelData)

object ComputationInputData {
  final case class ElectionLevelData(divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                     groupsAndCandidates: SenateGroupsAndCandidates,
                                     howtoVoteCards: Set[SenateHtv])

  final case class StateLevelData(countData: SenateCountData)
}
