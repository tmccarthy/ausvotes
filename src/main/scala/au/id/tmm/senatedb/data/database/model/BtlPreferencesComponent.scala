package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.database.DriverComponent
import au.id.tmm.senatedb.model.{CandidatePosition, Preference, Preferenceable}
import au.id.tmm.utilities.string.StringUtils.ImprovedString

final case class BtlPreferencesRow(ballotId: String,
                                   group: String,
                                   groupPosition: Int,
                                   preference: Option[Int],
                                   mark: Option[Char]) extends Preferenceable {
  def position: CandidatePosition = CandidatePosition(group, groupPosition)

  override def parsedPreference: Preference = Preference.fromOneOf(preference, mark)
}

object BtlPreferencesRow {
  def apply(ballotId: String, candidatePosition: CandidatePosition, preference: Preference): BtlPreferencesRow =
    BtlPreferencesRow(ballotId, candidatePosition.group, candidatePosition.positionInGroup, preference.asNumber, preference.asChar)

  def tupled(tuple: (String, String, Int, Option[Int], Option[Char])): BtlPreferencesRow = tuple match {
    case (ballotId, group, groupPosition, preference, mark) =>
      BtlPreferencesRow(ballotId, group.rtrim, groupPosition, preference, mark)
  }
}

trait BtlPreferencesComponent { this: DriverComponent with BallotComponent with ComponentUtilities =>
  import driver.api._

  class BtlPreferencesTable(tag: Tag) extends Table[BtlPreferencesRow](tag, "BtlPreferences") with CommonColumns {
    def ballotId = ballotIdColumn()

    def group = groupColumn()
    def groupPosition = positionInGroupColumn()

    def preference = preferenceColumn()
    def mark = markColumn()

    def pk = primaryKey("PK_BTL_PREFERENCE", (ballotId, group, groupPosition))

    def * = (ballotId, group, groupPosition, preference, mark) <>
      (BtlPreferencesRow.tupled, BtlPreferencesRow.unapply)
  }

  val btlPreferences: TableQuery[BtlPreferencesTable] = TableQuery[BtlPreferencesTable]

  def btlPreferencesFor(ballotId: String) = btlPreferences.filter(_.ballotId === ballotId)

  def insertBtlPreferences(toInsert: Iterable[BtlPreferencesRow]) = btlPreferences ++= toInsert
}