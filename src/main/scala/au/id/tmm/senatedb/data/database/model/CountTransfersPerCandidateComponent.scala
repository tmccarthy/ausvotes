package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.database.DriverComponent
import au.id.tmm.utilities.string.StringUtils.ImprovedString

final case class CountTransfersPerCandidateRow(election: String,
                                               state: String,
                                               count: Int,
                                               group: String,
                                               positionInGroup: Int,

                                               papers: Int,
                                               votesTransferred: Int,
                                               votesTotal: Int)

object CountTransfersPerCandidateRow {
  def tupled(tuple: (String, String, Int, String, Int, Int, Int, Int)): CountTransfersPerCandidateRow = {
    tuple match {
      case (election, state, count, group, positionInGroup, papers, votesTransferred, votesTotal) =>
        CountTransfersPerCandidateRow(election, state.rtrim, count, group.rtrim, positionInGroup, papers,
          votesTransferred, votesTotal)
    }
  }
}

trait CountTransfersPerCandidateComponent { this: DriverComponent with ComponentUtilities =>

  import driver.api._

  class CountTransfersPerCandidateTable(tag: Tag)
    extends Table[CountTransfersPerCandidateRow](tag, "CountTransfersPerCandidate")
      with CommonColumns {

    def election = electionIdColumn()
    def state = stateColumn()
    def count = countColumn()
    def group = groupColumn()
    def positionInGroup = positionInGroupColumn()

    def papers = column[Int]("papers")
    def votesTransferred = column[Int]("votesTranferred")
    def votesTotal = column[Int]("votesTotal")

    def pk = primaryKey("COUNT_TRANSFER_PER_CANDIDATE_PK", (election, state, count, group, positionInGroup))

    def * = (election, state, count, group, positionInGroup, papers, votesTransferred, votesTotal) <>
      (CountTransfersPerCandidateRow.tupled, CountTransfersPerCandidateRow.unapply)

  }
}
