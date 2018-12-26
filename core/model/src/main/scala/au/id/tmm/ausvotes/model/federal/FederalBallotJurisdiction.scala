package au.id.tmm.ausvotes.model.federal

import au.id.tmm.utilities.geo.australia.State

final case class FederalBallotJurisdiction(
                                            state: State,
                                            electorate: Division,
                                            voteCollectionPoint: FederalVcp,
                                          )
