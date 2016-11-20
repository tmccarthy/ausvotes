package au.id.tmm.senatedb.core.computations

import au.id.tmm.senatedb.core.model.{CountData, DivisionsAndPollingPlaces, GroupsAndCandidates}

final case class ComputationInputData(allgroupsAndCandidates: GroupsAndCandidates,
                                      allDivisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                      countDataForState: CountData) {

}
