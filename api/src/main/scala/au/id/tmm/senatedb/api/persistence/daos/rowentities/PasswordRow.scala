package au.id.tmm.senatedb.api.persistence.daos.rowentities

import com.mohiva.play.silhouette.api.util.PasswordInfo
import scalikejdbc._

private[daos] final case class PasswordRow(
                                            id: Long,
                                            loginInfo: LoginInfoRow,
                                            hasher: String,
                                            password: String,
                                            salt: Option[String],
                                          ) {
  def asPasswordInfo: PasswordInfo = PasswordInfo(hasher, password, salt)
}

private[daos] object PasswordRow extends SQLSyntaxSupport[PasswordRow] {

  override def tableName: String = "password"

  def apply(p: SyntaxProvider[PasswordRow], l: SyntaxProvider[LoginInfoRow])(rs: WrappedResultSet): PasswordRow =
    apply(p.resultName, l.resultName)(rs)

  def apply(p: ResultName[PasswordRow], l: ResultName[LoginInfoRow])(rs: WrappedResultSet): PasswordRow =
    PasswordRow(
      id = rs.long(p.id),
      loginInfo = LoginInfoRow(l)(rs),
      hasher = rs.string(p.hasher),
      password = rs.string(p.password),
      salt = rs.stringOpt(p.salt),
    )
}