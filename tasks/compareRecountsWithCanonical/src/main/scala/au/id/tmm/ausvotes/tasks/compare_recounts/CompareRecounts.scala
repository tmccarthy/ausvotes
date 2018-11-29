package au.id.tmm.ausvotes.tasks.compare_recounts

import au.id.tmm.ausvotes.core.model.parsing.CandidatePosition
import au.id.tmm.countstv.model.CompletedCount
import scalaz.zio
import scalaz.zio.IO

object CompareRecounts extends zio.App {

  override def run(args: List[String]): IO[Nothing, ExitStatus] = ???

  def compareRecounts(
                       canonicalCount: CompletedCount[CandidatePosition],
                       computedCount: CompletedCount[CandidatePosition],
                     ): CountComparison = ???

}
