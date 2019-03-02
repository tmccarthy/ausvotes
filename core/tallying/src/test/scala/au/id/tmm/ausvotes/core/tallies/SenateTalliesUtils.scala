package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.computations.StvBallotWithFacts
import au.id.tmm.ausvotes.core.tallies.typeclasses.Tallier
import au.id.tmm.ausvotes.model.federal.FederalBallotJurisdiction
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallotId, SenateElectionForState}
import cats.Monoid

object SenateTalliesUtils {

  def tallyFor[A : Monoid](senateElectionTallier: SenateElectionTalliers.BallotTallier[A])(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): A =
    Tallier[SenateElectionTalliers.BallotTallier[A], StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId], A].tally(senateElectionTallier)(ballot)

  def isCounted[A : Monoid : Numeric](senateElectionTallier: SenateElectionTalliers.BallotTallier[A])(ballot: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Boolean =
    tallyFor(senateElectionTallier)(ballot) == implicitly[Numeric[A]].one

  implicit val monoidForLong: Monoid[Long] = cats.instances.long.catsKernelStdGroupForLong
  implicit val monoidForDouble: Monoid[Double] = cats.instances.double.catsKernelStdGroupForDouble

}
