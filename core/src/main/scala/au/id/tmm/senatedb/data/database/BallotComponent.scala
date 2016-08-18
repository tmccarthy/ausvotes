package au.id.tmm.senatedb.data.database

final case class BallotRow(ballotId: String,
                           electionId: String,
                           state: String,
                           electorate: String,
                           voteCollectionPointId: Int,
                           batchNo: Int,
                           paperNo: Int)

trait BallotComponent { this: DriverComponent with BtlPreferencesComponent with AtlPreferencesComponent =>
  import driver.api._

  class BallotTable(tag: Tag) extends Table[BallotRow](tag, "Ballots") {
    def ballotId = column[String]("ballotId")

    def electionId = column[String]("electionId")
    def state = column[String]("state")

    def electorate = column[String]("electorate")
    def voteCollectionPointId = column[Int]("voteCollectionPointId")
    def batchNo = column[Int]("batchNo")
    def paperNo = column[Int]("paperNo")

    def * = (ballotId,
      electionId,
      state,
      electorate,
      voteCollectionPointId,
      batchNo,
      paperNo) <> (BallotRow.tupled, BallotRow.unapply)
  }

  val ballots: TableQuery[BallotTable] = TableQuery[BallotTable]

  def insertBallot(toInsert: BallotRow) = ballots += toInsert

  def insertBallots(toInsert: Iterable[BallotRow]) = ballots ++= toInsert
}
