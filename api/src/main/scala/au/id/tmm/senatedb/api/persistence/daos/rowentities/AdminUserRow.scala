package au.id.tmm.senatedb.api.persistence.daos.rowentities

import au.id.tmm.senatedb.api.authentication.admin.AdminUser
import scalikejdbc._

final case class AdminUserRow(id: Long,
                              username: String,
                              loginInfo: LoginInfoRow,
                             ) {
  def asAdminUser: AdminUser = AdminUser(username, loginInfo.asLoginInfo)
}

object AdminUserRow extends SQLSyntaxSupport[AdminUserRow] {

  override def tableName: String = "admin_user"

  def apply(a: SyntaxProvider[AdminUserRow], l: SyntaxProvider[LoginInfoRow])(rs: WrappedResultSet): AdminUserRow =
    apply(a.resultName, l.resultName)(rs)

  def apply(a: SQLSyntaxProvider[AdminUserRow], l: ResultName[LoginInfoRow])(rs: WrappedResultSet): AdminUserRow =
    AdminUserRow(
      id = rs.long(a.id),
      username = rs.string(a.username),
      loginInfo = LoginInfoRow(l)(rs),
    )
}