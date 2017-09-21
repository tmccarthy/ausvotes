package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.authentication.admin.AdminUser
import au.id.tmm.senatedb.api.persistence.daos.rowentities.{AdminUserRow, LoginInfoRow}
import au.id.tmm.senatedb.api.services.exceptions.NoSuchAdminUserException
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import org.postgresql.util.PSQLException
import scalikejdbc.{DB, _}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AdminUserDao @Inject() (loginInfoDao: LoginInfoDao)(implicit ec: ExecutionContext) {

  private val (a, l) = (AdminUserRow.syntax, LoginInfoRow.syntax)

  def withLoginInfo(loginInfo: LoginInfo): Future[Option[AdminUser]] = Future {
    DB.localTx { implicit session =>
      withSQL(
        select
          .from(AdminUserRow as a)
          .innerJoin(LoginInfoRow as l)
          .on(a.loginInfo, l.id)
          .where
          .eq(l.providerId, loginInfo.providerID)
          .and
          .eq(l.providerKey, loginInfo.providerKey)
      )
        .map(AdminUserRow(a, l))
        .headOption()
        .apply()
        .map(_.asAdminUser)
    }
  }

  def write(adminUser: AdminUser): Future[Unit] = Future {
    DB.localTx { implicit session =>
      val loginInfoId = loginInfoDao.upsert(adminUser.loginInfo).id

      val a = AdminUserRow.column

      val statement = withSQL(
        insert
          .into(AdminUserRow)
          .namedValues(
            a.username -> adminUser.username,
            a.loginInfo -> loginInfoId,
          )
      )
        .update()

      try {
        statement.apply()
      } catch {
        case e: PSQLException if e.getMessage startsWith "ERROR: duplicate key value violates unique constraint \"uk_admin_username\"" =>
          throw new IllegalStateException(s"Admin user ${adminUser.username} already exists", e)
      }
    }
  }

  def remove(adminUser: AdminUser): Future[Unit] = Future {
    DB.localTx { implicit session =>

      val numRowsDeleted = withSQL(
        delete
          .from(AdminUserRow as a)
          .where
          .eq(a.username, adminUser.username)
      )
        .update()
        .apply()

      if (numRowsDeleted < 1) {
        throw NoSuchAdminUserException(adminUser)
      }
    }
  }

}
