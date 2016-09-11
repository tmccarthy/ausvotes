package au.id.tmm.senatedb.model

import com.google.common.collect.ComparisonChain

final case class CandidatePosition(group: String, positionInGroup: Int) extends Ordered[CandidatePosition] {
  override def compare(that: CandidatePosition): Int = {
    ComparisonChain.start()
      .compare(group, that.group)
      .compare(positionInGroup, that.positionInGroup)
      .result()
  }
}
