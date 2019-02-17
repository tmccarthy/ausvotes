package au.id.tmm.ausvotes.model.stv

import au.id.tmm.countstv.normalisation.BallotNormalisationRules

final case class StvNormalisationRules(
                                        atlOptionalRules: BallotNormalisationRules,
                                        atlMandatoryRules: BallotNormalisationRules,

                                        btlOptionalRules: BallotNormalisationRules,
                                        btlMandatoryRules: BallotNormalisationRules,
                                      )
