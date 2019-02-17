package au.id.tmm.ausvotes.model.stv

import au.id.tmm.countstv.normalisation.Preference

final case class Ballot[E, J, I](
                                  election: E,
                                  jurisdiction: J,
                                  id: I,
                                  groupPreferences: Map[Group[E], Preference],
                                  candidatePreferences: Map[StvCandidate[E], Preference],
                                )
