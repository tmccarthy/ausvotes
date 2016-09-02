package au.id.tmm.senatedb.data.database

import au.id.tmm.senatedb.data.BallotId
import au.id.tmm.utilities.string.StringUtils.ImprovedString

final case class AtlPreferencesRow(ballotId: String,
                                   group: String,
                                   preference: Option[Int],
                                   mark: Option[Char])

object AtlPreferencesRow {
  def tupled(tuple: (String, String, Option[Int], Option[Char])): AtlPreferencesRow = tuple match {
    case (ballotId, group, preference, mark) => AtlPreferencesRow(ballotId, group.rtrim, preference, mark)
  }
}

trait AtlPreferencesComponent { this: DriverComponent with BallotComponent =>
  import driver.api._

  class AtlPreferencesTable(tag: Tag) extends Table[AtlPreferencesRow](tag, "AtlPreferences") {
    def ballotId = column[String]("ballotId", O.Length(BallotId.length, varying = false))

    def group = column[String]("group", O.Length(2, varying = false))

    def preference = column[Option[Int]]("preference")
    def specialChar = column[Option[Char]]("specialChar")

    def pk = primaryKey("PK_ATL_BALLOT", (ballotId, group))

    def * = (ballotId, group, preference, specialChar) <> (AtlPreferencesRow.tupled, AtlPreferencesRow.unapply)
  }

  val atlPreferences: TableQuery[AtlPreferencesTable] = TableQuery[AtlPreferencesTable]

  def atlPreferencesFor(ballotId: String) = atlPreferences.filter(_.ballotId === ballotId)

  def insertAtlPreferences(toInsert: Iterable[AtlPreferencesRow]) = atlPreferences ++= toInsert
}
