package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.database.DriverComponent
import au.id.tmm.senatedb.model.{Preference, Preferenceable}
import au.id.tmm.utilities.string.StringUtils.ImprovedString

final case class AtlPreferencesRow(ballotId: String,
                                   group: String,
                                   preference: Option[Int],
                                   mark: Option[Char]) extends Preferenceable {
  override def parsedPreference: Preference = Preference.fromOneOf(preference, mark)
}

object AtlPreferencesRow {
  def tupled(tuple: (String, String, Option[Int], Option[Char])): AtlPreferencesRow = tuple match {
    case (ballotId, group, preference, mark) => AtlPreferencesRow(ballotId, group.rtrim, preference, mark)
  }
}

trait AtlPreferencesComponent { this: DriverComponent with BallotComponent with ComponentUtilities =>
  import driver.api._

  class AtlPreferencesTable(tag: Tag) extends Table[AtlPreferencesRow](tag, "AtlPreferences") with CommonColumns {
    def ballotId = ballotIdColumn()

    def group = groupColumn()

    def preference = preferenceColumn()
    def mark = markColumn()

    def pk = primaryKey("PK_ATL_BALLOT", (ballotId, group))

    def * = (ballotId, group, preference, mark) <> (AtlPreferencesRow.tupled, AtlPreferencesRow.unapply)
  }

  val atlPreferences: TableQuery[AtlPreferencesTable] = TableQuery[AtlPreferencesTable]

  def atlPreferencesFor(ballotId: String) = atlPreferences.filter(_.ballotId === ballotId)

  def insertAtlPreferences(toInsert: Iterable[AtlPreferencesRow]) = atlPreferences ++= toInsert
}
