package au.id.tmm.ausvotes.model.nsw.legco

import au.id.tmm.ausvotes.model.nsw
import au.id.tmm.ausvotes.model.nsw.NswVoteCollectionPoint

final case class BallotJurisdiction(
                                     district: nsw.District,
                                     vcp: NswVoteCollectionPoint,
                                   )
