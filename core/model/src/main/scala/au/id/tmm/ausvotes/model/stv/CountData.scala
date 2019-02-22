package au.id.tmm.ausvotes.model.stv

import au.id.tmm.countstv.model.CompletedCount

final case class CountData[E](
                               election: E,
                               completedCount: CompletedCount[StvCandidate[E]],
                             )
