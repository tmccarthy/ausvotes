package au.id.tmm.senatedb.model.parsing

final case class CandidatePosition(group: BallotGroup, positionInGroup: Int) extends Ordered[CandidatePosition] {
  override def compare(that: CandidatePosition): Int = CandidatePosition.ordering.compare(this, that)
}

object CandidatePosition {
  private val ordering: Ordering[CandidatePosition] = Ordering.by(pos => (pos.group.index, pos.positionInGroup))
}