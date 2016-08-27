package au.id.tmm.senatedb.data.database

final case class AtlPreferencesRow(ballotId: String,
                                   group: String,
                                   preference: Option[Int],
                                   mark: Option[Char])

trait AtlPreferencesComponent { this: DriverComponent with BallotComponent =>
  import driver.api._

  class AtlPreferencesTable(tag: Tag) extends Table[AtlPreferencesRow](tag, "AtlPreferences") {
    def ballotId = column[String]("ballotId")

    def group = column[String]("group")

    def preference = column[Option[Int]]("preference")
    def specialChar = column[Option[Char]]("specialChar")

    def pk = primaryKey("PK_ATL_BALLOT", (ballotId, group))

    def joinedBallot = foreignKey("FK_ATL_BALLOT", ballotId, ballots)(_.ballotId)

    def * = (ballotId, group, preference, specialChar) <> (AtlPreferencesRow.tupled, AtlPreferencesRow.unapply)

  }

  val atlPreferences: TableQuery[AtlPreferencesTable] = TableQuery[AtlPreferencesTable]

  def atlPreferencesFor(ballotId: String) = atlPreferences.filter(_.ballotId === ballotId)

  def insertAtlPreferences(toInsert: Iterable[AtlPreferencesRow]) = atlPreferences ++= toInsert
}
