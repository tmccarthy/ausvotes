package au.id.tmm.senatedb.database.model

final case class AtlPreferencesRow(ballotId: String,
                                   group: String,
                                   preference: Int)

trait AtlPreferencesComponent { this: DriverComponent with BallotComponent =>
  import driver.api._

  class AtlPreferencesTable(tag: Tag) extends Table[AtlPreferencesRow](tag, "AtlPreferencesTable") {
    def ballotId = column[String]("ballotId", O.PrimaryKey)

    def group = column[String]("group")
    def preference = column[Int]("preference")

    def joinedBallot = foreignKey("FK_BALLOT", ballotId, ballots)(_.ballotId)

    def * = (ballotId, group, preference) <> (AtlPreferencesRow.tupled, AtlPreferencesRow.unapply)

  }

  def atlPreferences = TableQuery[AtlPreferencesTable]

}
