package au.id.tmm.senatedb.data.database

import slick.driver.JdbcProfile

trait DriverComponent {
  val driver: JdbcProfile
}
