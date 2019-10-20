package au.id.tmm.ausvotes.model.federal

import au.id.tmm.ausgeo.State

final case class FederalBallotJurisdiction(
                                            state: State,
                                            electorate: Division,
                                            voteCollectionPoint: FederalVcp,
                                          )
