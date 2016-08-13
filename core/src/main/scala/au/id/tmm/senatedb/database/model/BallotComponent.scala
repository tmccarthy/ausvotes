package au.id.tmm.senatedb.database.model

final case class BallotRow(ballotId: Long,
                           electionId: String,
                           state: String,
                           batchNo: Int,
                           paperNo: Int)

trait BallotComponent { this: DriverComponent with PreferencesComponent =>
  import driver.api._

  class BallotTable(tag: Tag) extends Table[BallotRow](tag, "Ballots") {
    def ballotId = column[Long]("ballotId")

    def electionId = column[String]("electionId")
    def state = column[String]("state")

    def batchNo = column[Int]("batchNo")
    def paperNo = column[Int]("paperNo")

    def joinedPreferences = foreignKey("FK_PREFERENCES", ballotId, preferences)(_.ballotId)

    def * = (ballotId, electionId, state, batchNo, paperNo) <> (BallotRow.tupled, BallotRow.unapply)
  }

  val ballots = TableQuery[BallotTable]
}
