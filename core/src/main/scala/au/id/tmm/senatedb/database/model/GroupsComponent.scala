package au.id.tmm.senatedb.database.model

final case class GroupsRow(groupId: String,
                           election: String,
                           state: String,
                           groupPosition: Int)

trait GroupsComponent { this: DriverComponent =>

  import driver.api._

  class GroupsTable(tag: Tag) extends Table[GroupsRow](tag, "Groups") {
    def groupId = column[String]("groupId")
    def election = column[String]("election")
    def state = column[String]("state")

    def groupPosition = column[Int]("groupPosition")

    def pk = primaryKey("PK_GROUPS", (groupId, election, state))

    def * = (groupId, election, state, groupPosition) <> (GroupsRow.tupled, GroupsRow.unapply)

  }

  def groups = TableQuery[GroupsTable]

}
