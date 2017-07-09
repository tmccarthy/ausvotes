package au.id.tmm.senatedb.api.persistence.daos

trait RowConversions {

  def aliasedColumnName(alias: String)(columnName: String): String = {
    if (alias.isEmpty) {
      columnName
    } else {
      s"$alias.$columnName"
    }
  }

}