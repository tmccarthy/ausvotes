package au.id.tmm.senatedb.data.database

import au.id.tmm.senatedb.data.BallotId

final case class BtlPreferencesRow(ballotId: String,
                                   group: String,
                                   groupPosition: Int,
                                   preference: Option[Int],
                                   mark: Option[Char])

trait BtlPreferencesComponent { this: DriverComponent with BallotComponent =>
  import driver.api._

  class BtlPreferencesTable(tag: Tag) extends Table[BtlPreferencesRow](tag, "BtlPreferences") {
    def ballotId = column[String]("ballotId", O.Length(BallotId.length, varying = false))

    def group = column[String]("group", O.Length(2, varying = false))
    def groupPosition = column[Int]("groupPosition")

    def preference = column[Option[Int]]("preference")
    def specialChar = column[Option[Char]]("specialChar")

    def pk = primaryKey("PK_BTL_PREFERENCE", (ballotId, group, groupPosition))

    def joinedBallot = foreignKey("FK_BTL_BALLOT", ballotId, ballots)(_.ballotId)

    def * = (ballotId, group, groupPosition, preference, specialChar) <>
      (BtlPreferencesRow.tupled, BtlPreferencesRow.unapply)
  }

  val btlPreferences: TableQuery[BtlPreferencesTable] = TableQuery[BtlPreferencesTable]

  def btlPreferencesFor(ballotId: String) = btlPreferences.filter(_.ballotId === ballotId)

  def insertBtlPreferences(toInsert: Iterable[BtlPreferencesRow]) = btlPreferences ++= toInsert
}