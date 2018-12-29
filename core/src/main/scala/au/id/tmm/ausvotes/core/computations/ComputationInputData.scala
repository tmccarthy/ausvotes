package au.id.tmm.ausvotes.core.computations

import au.id.tmm.ausvotes.core.computations.ComputationInputData.{ElectionLevelData, StateLevelData}
import au.id.tmm.ausvotes.core.model.{DivisionsAndPollingPlaces, GroupsAndCandidates}
import au.id.tmm.ausvotes.model.federal.senate.{SenateCountData, SenateHtv}

final case class ComputationInputData(electionLevel: ElectionLevelData, stateLevel: StateLevelData)

object ComputationInputData {
  final case class ElectionLevelData(divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                     groupsAndCandidates: GroupsAndCandidates,
                                     howtoVoteCards: Set[SenateHtv])

  final case class StateLevelData(countData: SenateCountData)
}
