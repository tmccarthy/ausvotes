package au.id.tmm.senatedb.model

final case class CandidatePosition(group: String, positionInGroup: Int) extends Ordered[CandidatePosition] {
  private lazy val groupIndex: Int = GroupUtils.indexOfGroup(group)

  override def compare(that: CandidatePosition): Int = CandidatePosition.ordering.compare(this, that)
}

object CandidatePosition {
  private val ordering: Ordering[CandidatePosition] = Ordering.by(pos => (pos.groupIndex, pos.positionInGroup))
}