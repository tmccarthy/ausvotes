package au.id.tmm.senatedb.database.model

import slick.driver.JdbcProfile

private[database] final class DAL(val driver: JdbcProfile)
  extends DriverComponent with BallotComponent with BtlPreferencesComponent {

  import driver.api._

  def create() = {
    (ballots.schema ++ btlPreferences.schema).create
  }
}

private[database] object DAL {
  val UNKNOWN: Int = -1
}