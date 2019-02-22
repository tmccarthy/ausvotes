package au.id.tmm.ausvotes.model

import cats.data.NonEmptyVector

final case class HowToVoteCard[E, C](
                                      election: E,
                                      issuer: C,
                                      suggestedOrder: NonEmptyVector[C],
                                    )
