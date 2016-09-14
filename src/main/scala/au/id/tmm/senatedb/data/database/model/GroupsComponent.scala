package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.database.DriverComponent
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.string.StringUtils.ImprovedString

final case class GroupsRow(groupId: String,
                           election: String,
                           state: String,
                           party: String)

object GroupsRow {
  def tupled(tuple: (String, String, String, String)): GroupsRow = tuple match {
    case (groupId, election, state, party) => GroupsRow(groupId.rtrim, election, state.rtrim, party.rtrim)
  }
}

trait GroupsComponent { this: DriverComponent with ComponentUtilities =>

  import driver.api._

  class GroupsTable(tag: Tag) extends Table[GroupsRow](tag, "Groups") with CommonColumns {
    def groupId = groupColumn()
    def election = electionIdColumn()
    def state = stateColumn()

    def party = partyColumn()

    def pk = primaryKey("PK_GROUPS", (groupId, election, state))

    def * = (groupId, election, state, party) <> (GroupsRow.tupled, GroupsRow.unapply)

  }

  val groups: TableQuery[GroupsTable] = TableQuery[GroupsTable]

  def insertGroups(toInsert: Set[GroupsRow]) = groups ++= toInsert

  def groupsForElection(election: SenateElection) = groups
    .filter(group => group.election === election.aecID)
}
