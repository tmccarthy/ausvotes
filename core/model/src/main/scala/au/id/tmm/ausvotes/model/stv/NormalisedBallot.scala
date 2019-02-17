package au.id.tmm.ausvotes.model.stv

import au.id.tmm.ausvotes.model.instances.BallotNormalisationResultInstances.Ops
import au.id.tmm.countstv.normalisation.BallotNormalisation

final case class NormalisedBallot[E](
                                      atl: BallotNormalisation.Result[Group[E]],
                                      atlCandidateOrder: Option[Vector[StvCandidate[E]]],

                                      btl: BallotNormalisation.Result[StvCandidate[E]],

                                      canonicalOrder: Option[Vector[StvCandidate[E]]],
                                    ) {

  val isNormalisedToAtl: Boolean = canonicalOrder.nonEmpty && canonicalOrder == atlCandidateOrder
  val isNormalisedToBtl: Boolean = canonicalOrder.nonEmpty && canonicalOrder == btl.normalisedBallotIfFormal

}
