package au.id.tmm.senatedb.api.persistence.entities.stats

import au.id.tmm.senatedb.core.tallies._

trait StatClass {

  def requiredTalliers: Set[Tallier]

  def statsFromTallyBundle(tallyBundle: TallyBundle): Vector[Stat[Any]]

}

object StatClass {

  trait SimpleCountStatClass extends StatClass {
    def ballotCounter: BallotCounter

    def hasPerCapita: Boolean = true

    private [stats] lazy val nationalTallier = TallierBuilder
      .counting(ballotCounter)
      .groupedBy(BallotGrouping.SenateElection)

    private [stats] def nationalCapitaTallier = if (hasPerCapita) Some(FormalBallots.nationalTallier) else None

    private [stats] lazy val stateTallier = TallierBuilder
      .counting(ballotCounter)
      .groupedBy(
        BallotGrouping.SenateElection,
        BallotGrouping.State
      )

    private [stats] def stateCapitaTallier = if (hasPerCapita) Some(FormalBallots.stateTallier) else None

    private [stats] lazy val divisionTallier = TallierBuilder
      .counting(ballotCounter)
      .groupedBy(
        BallotGrouping.SenateElection,
        BallotGrouping.State,
        BallotGrouping.Division
      )

    private [stats] def divisionCapitaTallier = if (hasPerCapita) Some(FormalBallots.divisionTallier) else None

    private [stats] lazy val vcpTallier = TallierBuilder
      .counting(ballotCounter)
      .groupedBy(
        BallotGrouping.SenateElection,
        BallotGrouping.State,
        BallotGrouping.Division,
        BallotGrouping.VoteCollectionPoint
      )

    private [stats] def vcpCapitaTallier = if (hasPerCapita) Some(FormalBallots.vcpTallier) else None

    override lazy val requiredTalliers: Set[Tallier] = Stream(
      Some(nationalTallier), Some(stateTallier), Some(divisionTallier), Some(vcpTallier),
      nationalCapitaTallier, stateCapitaTallier, divisionCapitaTallier, vcpCapitaTallier
    ).flatten.toSet

    override def statsFromTallyBundle(tallyBundle: TallyBundle): Vector[Stat[Any]] = {
      (
        StatComputations.nationalStatsFor(this, nationalTallier, nationalCapitaTallier, tallyBundle) append
          StatComputations.stateStatsFor(this, stateTallier, stateCapitaTallier, tallyBundle) append
          StatComputations.divisionStatsFor(this, divisionTallier, divisionCapitaTallier, tallyBundle) append
          StatComputations.vcpStatsFor(this, vcpTallier, vcpCapitaTallier, tallyBundle)
        ).toVector
    }
  }

  case object FormalBallots extends SimpleCountStatClass {
    override def ballotCounter: BallotCounter = BallotCounter.FormalBallots

    override def hasPerCapita: Boolean = false
  }

  case object DonkeyVotes extends SimpleCountStatClass {
    override def ballotCounter: BallotCounter = BallotCounter.DonkeyVotes
  }

  case object VotedAtl extends SimpleCountStatClass {
    override def ballotCounter: BallotCounter = BallotCounter.VotedAtl
  }

  case object VotedAtlAndBtl extends SimpleCountStatClass {
    override def ballotCounter: BallotCounter = BallotCounter.VotedAtlAndBtl
  }

  case object VotedBtl extends SimpleCountStatClass {
    override def ballotCounter: BallotCounter = BallotCounter.VotedBtl
  }

  case object ExhaustedBallots extends SimpleCountStatClass {
    override def ballotCounter: BallotCounter = BallotCounter.ExhaustedBallots
  }

  case object ExhaustedVotes extends SimpleCountStatClass {
    override def ballotCounter: BallotCounter = BallotCounter.ExhaustedVotes
  }

  case object UsedHowToVoteCard extends SimpleCountStatClass {
    override def ballotCounter: BallotCounter = BallotCounter.UsedHowToVoteCard
  }

  case object Voted1Atl extends SimpleCountStatClass {
    override def ballotCounter: BallotCounter = BallotCounter.Voted1Atl
  }

  case object UsedSavingsProvision extends SimpleCountStatClass {
    override def ballotCounter: BallotCounter = BallotCounter.UsedSavingsProvision
  }
}