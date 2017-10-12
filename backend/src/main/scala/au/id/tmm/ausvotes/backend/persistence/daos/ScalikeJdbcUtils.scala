package au.id.tmm.ausvotes.backend.persistence.daos

import scalikejdbc._

private[daos] object ScalikeJdbcUtils {

  def joinWithCommas(parts: SQLSyntax*): SQLSyntax = SQLSyntax.join(parts, sqls",", spaceBeforeDelimier = true)

  implicit class InsertOps(builder: InsertSQLBuilder) {

    def onConflictDoNothing(conflictTargets: SQLSyntax*): scalikejdbc.InsertSQLBuilder = {
      val fieldList = joinWithCommas(conflictTargets: _*)

      builder.append(sqls"ON CONFLICT ($fieldList) DO NOTHING")
    }

    def onConflictUpdate(conflictTargets: SQLSyntax*)(columnsAndValues: (SQLSyntax, ParameterBinder)*): scalikejdbc.InsertSQLBuilder = {
      val fieldList = joinWithCommas(conflictTargets: _*)

      val setList = joinWithCommas(
        columnsAndValues.map { case(column, value) =>
          sqls"$column = $value"
        }: _*
      )

      builder.append(sqls"ON CONFLICT($fieldList) DO UPDATE SET $setList")
    }
  }

  implicit class DeleteOps(builder: DeleteSQLBuilder) {
    def using(tableName: TableAsAliasSQLSyntax): scalikejdbc.DeleteSQLBuilder = {
      builder.append(sqls"USING $tableName")
    }
  }
}
