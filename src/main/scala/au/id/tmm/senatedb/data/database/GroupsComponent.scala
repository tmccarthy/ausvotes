package au.id.tmm.senatedb.data.database

final case class GroupsRow(groupId: String,
                           election: String,
                           state: String,
                           party: String)

trait GroupsComponent { this: DriverComponent =>

  import driver.api._

  class GroupsTable(tag: Tag) extends Table[GroupsRow](tag, "Groups") {
    def groupId = column[String]("groupId")
    def election = column[String]("election")
    def state = column[String]("state")

    def party = column[String]("party")

    def pk = primaryKey("PK_GROUPS", (groupId, election, state))

    def * = (groupId, election, state, party) <> (GroupsRow.tupled, GroupsRow.unapply)

  }

  val groups: TableQuery[GroupsTable] = TableQuery[GroupsTable]

  def insertGroups(toInsert: Set[GroupsRow]) = groups ++= toInsert
}
