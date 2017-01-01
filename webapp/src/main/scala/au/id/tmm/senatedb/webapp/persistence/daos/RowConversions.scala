package au.id.tmm.senatedb.webapp.persistence.daos

trait RowConversions {

  def aliasedColumnName(alias: String)(columnName: String): String = {
    if (alias.isEmpty) {
      columnName
    } else {
      s"$alias.$columnName"
    }
  }

}