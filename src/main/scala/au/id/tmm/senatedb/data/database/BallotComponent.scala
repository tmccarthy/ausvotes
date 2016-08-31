package au.id.tmm.senatedb.data.database

import au.id.tmm.senatedb.data.BallotId

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
    def ballotId = column[String]("ballotId", O.Length(BallotId.length, varying = false))

    def electionId = column[String]("electionId", O.Length(5, varying = false))
    def state = column[String]("state", O.Length(3, varying = false))

    def electorate = column[String]("electorate", O.Length(15, varying = true))
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

  def ballotsWithId(id: String) = ballots.filter(_.ballotId === id)

  def insertBallot(toInsert: BallotRow) = ballots += toInsert

  def insertBallots(toInsert: Iterable[BallotRow]) = ballots ++= toInsert
}
