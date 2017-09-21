package au.id.tmm.senatedb.api.persistence.daos

import java.util.NoSuchElementException
import javax.inject.Singleton

import au.id.tmm.senatedb.api.persistence.daos.ScalikeJdbcUtils.{DeleteOps, InsertOps}
import au.id.tmm.senatedb.api.persistence.daos.rowentities.{LoginInfoRow, PasswordRow}
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PasswordDao @Inject() (loginInfoDao: LoginInfoDao)(implicit ec: ExecutionContext)
  extends DelegableAuthInfoDAO[PasswordInfo] {

  private val (p, l) = (PasswordRow.syntax, LoginInfoRow.syntax)

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = Future {
    DB.localTx { implicit session =>

      withSQL(
        select
          .from(PasswordRow as p)
          .innerJoin(LoginInfoRow as l).on(p.loginInfo, l.id)
          .where
          .eq(l.providerId, loginInfo.providerID)
          .and
          .eq(l.providerKey, loginInfo.providerKey)
          .limit(1)
      )
        .map(PasswordRow(p, l))
        .headOption()
        .apply()
        .map(_.asPasswordInfo)
    }
  }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future {
    DB.localTx { implicit session =>
      val loginInfoId = loginInfoDao.insert(loginInfo).id

      val passwordId = withSQL(
        insert
          .into(PasswordRow)
          .namedValues(
            sqls"login_info" -> loginInfoId,
            sqls"hasher" -> authInfo.hasher,
            sqls"password" -> authInfo.password,
            sqls"salt" -> authInfo.salt,
          )
      )
        .updateAndReturnGeneratedKey()
        .apply()

      authInfo
    }
  }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future {
    DB.localTx { implicit session =>
      val loginInfoId = loginInfoDao.rowOf(loginInfo)
        .map(_.id)
        .getOrElse(throw new NoSuchElementException(s"No loginInfo $loginInfo found"))

      withSQL(
        QueryDSL.update(PasswordRow)
          .set(
            sqls"hasher" -> authInfo.hasher,
            sqls"password" -> authInfo.password,
            sqls"salt" -> authInfo.salt,
          )
      )
        .update()
        .apply()

      authInfo
    }
  }

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future {

    DB.localTx { implicit session =>

      val loginInfoId = loginInfoDao.upsert(loginInfo).id

      withSQL(
        insert
          .into(PasswordRow)
          .namedValues(
            sqls"login_info" -> loginInfoId,
            sqls"hasher" -> authInfo.hasher,
            sqls"password" -> authInfo.password,
            sqls"salt" -> authInfo.salt,
          )
          .onConflictUpdate(sqls"login_info")(
            sqls"hasher" -> authInfo.hasher,
            sqls"password" -> authInfo.password,
            sqls"salt" -> authInfo.salt,
          )
      )
        .update()
        .apply()

      authInfo
    }
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = Future {
    DB.localTx { implicit session =>

      withSQL(
        delete
          .from(PasswordRow as p)
            .using(LoginInfoRow as l)
          .where
          .eq(l.id, p.loginInfo)
          .and
          .eq(l.providerId, loginInfo.providerID)
          .and
          .eq(l.providerKey, loginInfo.providerKey)
      )
//      val deleteStatement =
//        sqls"""DELETE FROM password
//              |  USING login_info
//              |WHERE
//              |  login_info.id = password.login_info AND
//              |  login_info.provider_id = ${loginInfo.providerID} AND
//              |  login_info.provider_key = ${loginInfo.providerKey}
//          """.stripMargin
//
//      withSQL(DeleteSQLBuilder(deleteStatement))
        .update()
        .apply()
    }
  }

}
