package au.id.tmm.senatedb.database.model

final case class BtlPreferencesRow(ballotId: String,
                                   candidateIndex: Int,
                                   preference: Int)

trait BtlPreferencesComponent { this: DriverComponent with BallotComponent =>
  import driver.api._

  class BtlPreferencesTable(tag: Tag) extends Table[BtlPreferencesRow](tag, "BtlPreferences") {
    def ballotId = column[String]("ballotId", O.PrimaryKey)

    def candidateOrdinal = column[Int]("candidateIndex")
    def preference = column[Int]("preference")

    def joinedBallot = foreignKey("FK_BALLOT", ballotId, ballots)(_.ballotId)

    def * = (ballotId, candidateOrdinal, preference) <>
      (BtlPreferencesRow.tupled, BtlPreferencesRow.unapply)
  }

  val btlPreferences = TableQuery[BtlPreferencesTable]
}