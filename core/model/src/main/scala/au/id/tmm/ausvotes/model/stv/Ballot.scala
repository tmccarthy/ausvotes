package au.id.tmm.ausvotes.model.stv

import au.id.tmm.ausvotes.model.Preference

final case class Ballot[E, C, J, I](
                                     election: E,
                                     jurisdiction: J,
                                     id: I,
                                     groupPreferences: Map[Group[E], Preference],
                                     candidatePreferences: Map[C, Preference],
                                   )
