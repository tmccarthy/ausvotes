package au.id.tmm.ausvotes.data_sources.aec.federal.resources

import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState

final case class SenateDistributionOfPreferencesResource(
                                                          senateElectionForState: SenateElectionForState,
                                                        )
