package au.id.tmm.senatedb.database.model

final case class BtlPreferencesRow(ballotId: String,
                                   group: String,
                                   groupPosition: Int,
                                   preference: Int)

trait BtlPreferencesComponent { this: DriverComponent with BallotComponent =>
  import driver.api._

  class BtlPreferencesTable(tag: Tag) extends Table[BtlPreferencesRow](tag, "BtlPreferences") {
    def ballotId = column[String]("ballotId")

    def group = column[String]("group")
    def groupPosition = column[Int]("groupPosition")

    def preference = column[Int]("preference")

    def pk = primaryKey("PK_BTL_PREFERENCE", (ballotId, group, groupPosition))

    def joinedBallot = foreignKey("FK_BALLOT", ballotId, ballots)(_.ballotId)

    def * = (ballotId, group, groupPosition, preference) <>
      (BtlPreferencesRow.tupled, BtlPreferencesRow.unapply)
  }

  val btlPreferences = TableQuery[BtlPreferencesTable]
}