package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.database.DriverComponent
import au.id.tmm.senatedb.data.database.model.DistributionOfPreferencesToCandidateRow.CandidateStatus
import au.id.tmm.senatedb.data.database.model.DistributionOfPreferencesToCandidateRow.CandidateStatus.CandidateStatus
import au.id.tmm.utilities.string.StringUtils.ImprovedString

final case class DistributionOfPreferencesToCandidateRow(election: String,
                                                         state: String,
                                                         count: Int,
                                                         group: String,
                                                         positionInGroup: Int,

                                                         papers: Int,
                                                         votesTransferred: Int,
                                                         votesTotal: Int,
                                                         status: CandidateStatus,
                                                         orderElected: Int)

object DistributionOfPreferencesToCandidateRow {

  def tupled(tuple: (String, String, Int, String, Int, Int, Int, Int, CandidateStatus, Int)): DistributionOfPreferencesToCandidateRow = {
    tuple match {
      case (election, state, count, group, positionInGroup, papers, votesTransferred, votesTotal, status, orderElected) =>
        DistributionOfPreferencesToCandidateRow(election, state.rtrim, count, group.rtrim, positionInGroup, papers,
          votesTransferred, votesTotal, status, orderElected)
    }
  }

  object CandidateStatus extends Enumeration {
    type CandidateStatus = Value

    val ELECTED = Value("e")
    val EXCLUDED = Value("x")
    val UNKNOWN = Value(" ")
  }
}

trait DistributionOfPreferencesToCandidateComponent { this: DriverComponent with ComponentUtilities =>

  import driver.api._

  implicit val candidateStatusMapper = MappedColumnType.base[CandidateStatus, Char](
    status => status.toString.charAt(0),
    char => CandidateStatus.withName(char.toString)
  )

  class DistributionOfPreferencesToCandidateTable(tag: Tag)
    extends Table[DistributionOfPreferencesToCandidateRow](tag, "DistributionOfPreferencesToCandidate")
      with CommonColumns {

    def election = electionIdColumn()
    def state = stateColumn()
    def count = countColumn()
    def group = groupColumn()
    def positionInGroup = positionInGroupColumn()

    def papers = column[Int]("papers")
    def votesTransferred = column[Int]("votesTranferred")
    def votesTotal = column[Int]("votesTotal")
    def status = column[CandidateStatus]("status")
    def orderElected = column[Int]("orderElected")

    def pk = primaryKey("DOP_TO_CANDIDATE_PK", (election, state, count, group, positionInGroup))

    def * = (election, state, count, group, positionInGroup, papers, votesTransferred, votesTotal, status, orderElected) <>
      (DistributionOfPreferencesToCandidateRow.tupled, DistributionOfPreferencesToCandidateRow.unapply)

  }
}
