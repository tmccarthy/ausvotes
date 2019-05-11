package au.id.tmm.ausvotes.model.nsw.legass

import au.id.tmm.ausvotes.model.nsw

final case class BallotJurisdiction(
                                     district: nsw.District,
                                     vcp: nsw.Vcp,
                                   )
