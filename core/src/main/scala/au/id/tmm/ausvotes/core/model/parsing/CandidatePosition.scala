package au.id.tmm.ausvotes.core.model.parsing

import au.id.tmm.ausvotes.core.model.GroupsAndCandidates

final case class CandidatePosition(group: BallotGroup, positionInGroup: Int) extends Ordered[CandidatePosition] {
  override def compare(that: CandidatePosition): Int =
    (this.group, this.positionInGroup) compare (that.group, that.positionInGroup)
}

object CandidatePosition {
  def constructBallotPositionLookup(groupsAndCandidates: GroupsAndCandidates): Map[Int, CandidatePosition] = {
    val numGroups = groupsAndCandidates.groups.size
    val candidatePositionsInBallotOrder = groupsAndCandidates.candidates.toStream
      .map(_.btlPosition)
      .sorted

    candidatePositionsInBallotOrder.zipWithIndex
      .map {
        case (candidatePosition, index) => (candidatePosition, index + numGroups + 1)
      }
      .map {
        case (candidatePosition, positionOnBallotOrdinal) => positionOnBallotOrdinal -> candidatePosition
      }
      .toMap
  }
}