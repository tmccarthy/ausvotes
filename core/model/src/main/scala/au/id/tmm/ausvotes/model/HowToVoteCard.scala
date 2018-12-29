package au.id.tmm.ausvotes.model

final case class HowToVoteCard[E, C](
                                      election: E,
                                      issuer: C,
                                      suggestedOrder: Vector[C],
                                    )
