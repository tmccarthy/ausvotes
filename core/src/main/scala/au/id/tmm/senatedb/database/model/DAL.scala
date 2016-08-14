package au.id.tmm.senatedb.database.model

import slick.driver.JdbcProfile

private[database] final class DAL(val driver: JdbcProfile) extends DriverComponent
  with GroupsComponent
  with CandidatesComponent
  with BallotComponent
  with AtlPreferencesComponent
  with BtlPreferencesComponent {

  import driver.api._

  def create() = {
    (groups.schema ++ candidates.schema ++ ballots.schema ++ atlPreferences.schema ++ btlPreferences.schema).create
  }
}

private[database] object DAL {
  val UNKNOWN: Int = -1
}