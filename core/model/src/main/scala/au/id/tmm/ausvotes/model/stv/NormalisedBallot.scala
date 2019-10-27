package au.id.tmm.ausvotes.model.stv

import au.id.tmm.ausvotes.model.instances.BallotNormalisationResultInstances.Ops
import au.id.tmm.countstv.normalisation.BallotNormalisation

import scala.collection.immutable.ArraySeq

final case class NormalisedBallot[E](
                                      atl: BallotNormalisation.Result[Group[E]],
                                      atlCandidateOrder: Option[ArraySeq[StvCandidate[E]]],

                                      btl: BallotNormalisation.Result[StvCandidate[E]],

                                      canonicalOrder: Option[ArraySeq[StvCandidate[E]]],
                                    ) {

  val isNormalisedToAtl: Boolean = canonicalOrder.nonEmpty && canonicalOrder == atlCandidateOrder
  val isNormalisedToBtl: Boolean = canonicalOrder.nonEmpty && canonicalOrder == btl.normalisedBallotIfFormal

}
