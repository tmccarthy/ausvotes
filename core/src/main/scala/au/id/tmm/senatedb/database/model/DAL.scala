package au.id.tmm.senatedb.database.model

import slick.driver.JdbcProfile

private[database] final class DAL(val driver: JdbcProfile)
  extends DriverComponent with BallotComponent with PreferencesComponent {

  import driver.api._

  def create() = {
    (ballots.schema ++ preferences.schema).create
  }
}
