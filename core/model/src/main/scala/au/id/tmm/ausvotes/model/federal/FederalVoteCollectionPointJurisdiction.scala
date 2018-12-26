package au.id.tmm.ausvotes.model.federal

import au.id.tmm.utilities.geo.australia.State

final case class FederalVoteCollectionPointJurisdiction(
                                                         state: State,
                                                         division: Division,
                                                       )
