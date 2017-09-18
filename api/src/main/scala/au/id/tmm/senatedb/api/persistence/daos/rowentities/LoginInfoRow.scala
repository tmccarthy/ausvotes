package au.id.tmm.senatedb.api.persistence.daos.rowentities

import com.mohiva.play.silhouette.api.LoginInfo
import scalikejdbc._

private[daos] final case class LoginInfoRow(
                                              id: Long,
                                              providerId: String,
                                              providerKey: String,
                                            ) {
  def asLoginInfo: LoginInfo = LoginInfo(providerId, providerKey)
}

private[daos] object LoginInfoRow extends SQLSyntaxSupport[LoginInfoRow] {

  override val tableName = "login_info"

  def apply(l: SyntaxProvider[LoginInfoRow])(rs: WrappedResultSet): LoginInfoRow = apply(l.resultName)(rs)

  def apply(l: SQLSyntaxProvider[LoginInfoRow])(rs: WrappedResultSet): LoginInfoRow = {
    LoginInfoRow(
      id = rs.long(l.id),
      providerId = rs.string(l.providerId),
      providerKey = rs.string(l.providerKey),
    )
  }

}