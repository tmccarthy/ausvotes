package au.id.tmm.senatedb.computations

import au.id.tmm.senatedb.model.{CountData, DivisionsAndPollingPlaces, GroupsAndCandidates}

final case class ComputationInputData(allgroupsAndCandidates: GroupsAndCandidates,
                                      allDivisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                      countDataForState: CountData) {

}
