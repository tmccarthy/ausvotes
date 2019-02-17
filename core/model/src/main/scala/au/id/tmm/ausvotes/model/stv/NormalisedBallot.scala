package au.id.tmm.ausvotes.model.stv

import au.id.tmm.ausvotes.model.instances.BallotNormalisationResultInstances.Ops
import au.id.tmm.countstv.normalisation.BallotNormalisation

final case class NormalisedBallot[E, C](
                                         atl: BallotNormalisation.Result[Group[E]],
                                         atlCandidateOrder: Option[Vector[C]],

                                         btl: BallotNormalisation.Result[C],

                                         canonicalOrder: Option[Vector[C]],
                                       ) {

  val isNormalisedToAtl: Boolean = canonicalOrder.nonEmpty && canonicalOrder == atlCandidateOrder
  val isNormalisedToBtl: Boolean = canonicalOrder.nonEmpty && canonicalOrder == btl.normalisedBallotIfFormal

}
