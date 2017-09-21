package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.persistence.daos.ScalikeJdbcUtils.InsertOps
import au.id.tmm.senatedb.api.persistence.daos.rowentities.LoginInfoRow
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import scalikejdbc._

@Singleton
private[daos] class LoginInfoDao @Inject() () {

  def rowOf(loginInfo: LoginInfo)(implicit session: DBSession): Option[LoginInfoRow] = {
    val l = LoginInfoRow.syntax

    withSQL(
      select
        .from(LoginInfoRow as l)
        .where
        .eq(l.providerId, loginInfo.providerID)
        .and
        .eq(l.providerKey, loginInfo.providerKey)
        .limit(1)
    )
      .map(LoginInfoRow(l))
      .headOption()
      .apply()
  }

  def insert(loginInfo: LoginInfo)(implicit session: DBSession): LoginInfoRow = {
    val l = LoginInfoRow.column

    withSQL(
      QueryDSL.insert
        .into(LoginInfoRow)
        .namedValues(
          l.providerId -> loginInfo.providerID,
          l.providerKey -> loginInfo.providerKey,
        )
        .returning(sqls"*")
    )
      .map(LoginInfoRow(l))
      .headOption()
      .apply()
      .get
  }

  def upsert(loginInfo: LoginInfo)(implicit session: DBSession): LoginInfoRow = {
    val l = LoginInfoRow.column

    withSQL(
      QueryDSL.insert
        .into(LoginInfoRow)
        .namedValues(
          l.providerId -> loginInfo.providerID,
          l.providerKey -> loginInfo.providerKey,
        )
        .onConflictUpdate(l.providerId, l.providerKey)(
          l.providerId -> loginInfo.providerID,
          l.providerKey -> loginInfo.providerKey,
        )
        .returning(sqls"*")
    )
      .map(LoginInfoRow(l))
      .headOption()
      .apply()
      .get
  }

}
