package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.model.parsing.{Division, Party, VoteCollectionPoint}
import au.id.tmm.utilities.geo.australia.State

import scala.collection.mutable

trait TallyReportGenerator { this: ReportGenerator =>

  override type T_REPORT = TallyReport

  override def generateFor(ballotsWithFacts: Vector[BallotWithFacts]): TallyReport = {
    var total: Long = 0
    val perState: mutable.Map[State, Long] = mutable.Map().withDefaultValue(0)
    val perDivision: mutable.Map[Division, Long] = mutable.Map().withDefaultValue(0)
    val perVoteCollectionPoint: mutable.Map[VoteCollectionPoint, Long] = mutable.Map().withDefaultValue(0)
    val perFirstPreferencedParty: mutable.Map[Option[Party], Long] = mutable.Map().withDefaultValue(0)

    for (ballotWithFacts <- ballotsWithFacts) {
      if (shouldCount(ballotWithFacts)) {
        total = total + 1
        perState.put(ballotWithFacts.ballot.state, perState(ballotWithFacts.ballot.state) + 1)
        perDivision.put(ballotWithFacts.ballot.division, perDivision(ballotWithFacts.ballot.division) + 1)
        perVoteCollectionPoint.put(ballotWithFacts.ballot.voteCollectionPoint, perVoteCollectionPoint(ballotWithFacts.ballot.voteCollectionPoint) + 1)
        perFirstPreferencedParty.put(ballotWithFacts.firstPreferencedParty, perFirstPreferencedParty(ballotWithFacts.firstPreferencedParty) + 1)
      }
    }

    TallyReport(total, perState.toMap, perDivision.toMap, perVoteCollectionPoint.toMap, perFirstPreferencedParty.toMap)
  }

  private[reporting] def shouldCount(ballotWithFacts: BallotWithFacts): Boolean
}
