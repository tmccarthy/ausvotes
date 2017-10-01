package au.id.tmm.senatedb.api.services

import au.id.tmm.senatedb.api.authentication.admin.AdminUser
import au.id.tmm.senatedb.api.persistence.daos.AdminUserDao
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.{LoginInfo, Provider}

import scala.concurrent.{ExecutionContext, Future}

class AdminUserService @Inject() (passwordHasherRegistry: PasswordHasherRegistry,
                                  adminUserDao: AdminUserDao,
                                  authInfoRepository: AuthInfoRepository,
                                 )(implicit ec: ExecutionContext) extends IdentityService[AdminUser] {

  def add(provider: Provider, username: String, password: String): Future[Unit] = {
    val loginInfo = LoginInfo(provider.id, username)

    val passwordInfo = passwordHasherRegistry.current.hash(password)

    authInfoRepository.add(loginInfo, passwordInfo).map(_ => Unit)
  }

  override def retrieve(loginInfo: LoginInfo): Future[Option[AdminUser]] = adminUserDao.withLoginInfo(loginInfo)

  def changePassword(adminUser: AdminUser, newPassword: String): Future[Unit] = {
    val newPasswordInfo = passwordHasherRegistry.current.hash(newPassword)

    authInfoRepository.update(adminUser.loginInfo, newPasswordInfo).map(_ => Unit)
  }

  def remove(adminUser: AdminUser): Future[Unit] = adminUserDao.remove(adminUser)
}
