package au.id.tmm.ausvotes.core.model.parsing

import au.id.tmm.ausvotes.core.model.GroupsAndCandidates

final case class CandidatePosition(group: BallotGroup, positionInGroup: Int) extends Ordered[CandidatePosition] {
  def code: String = s"${group.code}$positionInGroup"

  override def compare(that: CandidatePosition): Int =
    (this.group, this.positionInGroup) compare (that.group, that.positionInGroup)
}

object CandidatePosition {

  // TODO probably move this
  def constructBallotPositionLookup(groupsAndCandidates: GroupsAndCandidates): Map[Int, Candidate] = {
    val numGroups = groupsAndCandidates.groups.size
    val candidatesInBallotOrder = groupsAndCandidates.candidates.toStream
      .sorted

    candidatesInBallotOrder.zipWithIndex
      .map {
        case (candidate, index) => (candidate, index + numGroups + 1)
      }
      .map {
        case (candidate, positionOnBallotOrdinal) => positionOnBallotOrdinal -> candidate
      }
      .toMap
  }
}
