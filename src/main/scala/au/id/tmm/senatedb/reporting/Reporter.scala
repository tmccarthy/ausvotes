package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.data.database.Persistence

import scala.concurrent.ExecutionContext

class Reporter private (val persistence: Persistence)(implicit val ec: ExecutionContext)
  extends ReportingUtilities
  with ReportsFormalBallots
  with ReportsDonkeyVotes{


}

object Reporter {
  def apply(persistence: Persistence)(implicit ec: ExecutionContext): Reporter = new Reporter(persistence)
}