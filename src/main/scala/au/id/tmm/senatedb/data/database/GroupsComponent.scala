package au.id.tmm.senatedb.data.database

final case class GroupsRow(groupId: String,
                           election: String,
                           state: String,
                           party: String)

trait GroupsComponent { this: DriverComponent =>

  import driver.api._

  class GroupsTable(tag: Tag) extends Table[GroupsRow](tag, "Groups") {
    def groupId = column[String]("groupId", O.Length(2, varying = false))
    def election = column[String]("election", O.Length(5, varying = false))
    def state = column[String]("state", O.Length(3, varying = false))

    def party = column[String]("party", O.Length(100, varying = true))

    def pk = primaryKey("PK_GROUPS", (groupId, election, state))

    def * = (groupId, election, state, party) <> (GroupsRow.tupled, GroupsRow.unapply)

  }

  val groups: TableQuery[GroupsTable] = TableQuery[GroupsTable]

  def insertGroups(toInsert: Set[GroupsRow]) = groups ++= toInsert
}
