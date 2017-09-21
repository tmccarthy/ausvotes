package au.id.tmm.senatedb.api.authentication.admin

import au.id.tmm.senatedb.api.persistence.daos.AdminUserDao
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService

import scala.concurrent.Future

class AdminUserService @Inject() (adminUserDao: AdminUserDao) extends IdentityService[AdminUser] {
  override def retrieve(loginInfo: LoginInfo): Future[Option[AdminUser]] = adminUserDao.withLoginInfo(loginInfo)
}
