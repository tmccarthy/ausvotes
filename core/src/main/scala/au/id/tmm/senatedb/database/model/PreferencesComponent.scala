package au.id.tmm.senatedb.database.model

final case class PreferencesRow(ballotId: Long,
                                candidateOrdinal: Int,
                                preference: Int)

trait PreferencesComponent { this: DriverComponent with BallotComponent =>
  import driver.api._

  class PreferencesTable(tag: Tag) extends Table[PreferencesRow](tag, "Preferences") {
    def ballotId = column[Long]("ballotId")

    def candidateOrdinal = column[Int]("candidateOrdinal")
    def preference = column[Int]("")

    def joinedBallot = foreignKey("FK_BALLOT", ballotId, ballots)(_.ballotId)

    def * = (ballotId, candidateOrdinal, preference) <>
      (PreferencesRow.tupled, PreferencesRow.unapply)
  }

  val preferences = TableQuery[PreferencesTable]
}