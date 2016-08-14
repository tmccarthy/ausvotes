package au.id.tmm.senatedb.database.model

final case class BallotRow(ballotId: String,
                           electionId: String,
                           state: String,
                           electorate: String,
                           voteCollectionPointId: Int,
                           batchNo: Int,
                           paperNo: Int,
                           formal: Boolean,
                           expiredAtCount: Int)

trait BallotComponent { this: DriverComponent with BtlPreferencesComponent =>
  import driver.api._

  class BallotTable(tag: Tag) extends Table[BallotRow](tag, "Ballots") {
    def ballotId = column[String]("ballotId")

    def electionId = column[String]("electionId")
    def state = column[String]("state")

    def electorate = column[String]("electorate")
    def voteCollectionPointId = column[Int]("voteCollectionPointId")
    def batchNo = column[Int]("batchNo")
    def paperNo = column[Int]("paperNo")

    def formal = column[Boolean]("formal")
    def expiredAtCount = column[Int]("expiredAtCount")

    def joinedBtlPreferences = foreignKey("FK_BTL_PREFERENCES", ballotId, btlPreferences)(_.ballotId)

    def * = (ballotId,
      electionId,
      state,
      electorate,
      voteCollectionPointId,
      batchNo,
      paperNo,
      formal,
      expiredAtCount) <> (BallotRow.tupled, BallotRow.unapply)
  }

  val ballots = TableQuery[BallotTable]
}
