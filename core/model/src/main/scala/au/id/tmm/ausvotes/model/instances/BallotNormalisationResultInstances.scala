package au.id.tmm.ausvotes.model.instances

import au.id.tmm.countstv.normalisation.BallotNormalisation
import au.id.tmm.countstv.normalisation.BallotNormalisation.Result

object BallotNormalisationResultInstances {

  // TODO probably move this onto countstv
  implicit class Ops[C](ballotNormalisationResult: BallotNormalisation.Result[C]) {
    @inline def normalisedBallotIfFormal: Option[Vector[C]] = ballotNormalisationResult match {
      case Result.Formal(normalisedBallot) => Some(normalisedBallot)
      case Result.Saved(normalisedBallot, _) => Some(normalisedBallot)
      case Result.Informal(_, _, _) => None
    }

    @inline def isSavedOrFormal: Boolean = ballotNormalisationResult match {
      case Result.Formal(_) | Result.Saved(_, _) => true
      case Result.Informal(_, _, _) => false
    }
  }

}
