package au.id.tmm.senatedb.core.computations

import au.id.tmm.senatedb.core.computations.ComputationInputData.{ElectionLevelData, StateLevelData}
import au.id.tmm.senatedb.core.model.{CountData, DivisionsAndPollingPlaces, GroupsAndCandidates, HowToVoteCard}

final case class ComputationInputData(electionLevel: ElectionLevelData, stateLevel: StateLevelData)

object ComputationInputData {
  final case class ElectionLevelData(divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                     groupsAndCandidates: GroupsAndCandidates,
                                     howtoVoteCards: Set[HowToVoteCard])

  final case class StateLevelData(countData: CountData)
}