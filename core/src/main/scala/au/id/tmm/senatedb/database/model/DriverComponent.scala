package au.id.tmm.senatedb.database.model

import slick.driver.JdbcProfile

trait DriverComponent {
  val driver: JdbcProfile
}
