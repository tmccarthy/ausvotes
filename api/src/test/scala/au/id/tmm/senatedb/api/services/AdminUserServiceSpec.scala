package au.id.tmm.senatedb.api.services

import au.id.tmm.senatedb.api.authentication.admin.AdminUser
import au.id.tmm.senatedb.api.persistence.daos.AdminUserDao
import au.id.tmm.utilities.concurrent.FutureUtils.await
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.{PasswordHasherRegistry, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.BasicAuthProvider
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.InMemoryAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import org.scalamock.scalatest.MockFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class AdminUserServiceSpec extends ImprovedFlatSpec with MockFactory {

  private val bCryptHasher = new BCryptPasswordHasher()
  private val passwordHasherRegistry: PasswordHasherRegistry = PasswordHasherRegistry(current = bCryptHasher)

  private val adminUserDao = mock[AdminUserDao]
  private val authInfoDao: InMemoryAuthInfoDAO[PasswordInfo] = new InMemoryAuthInfoDAO[PasswordInfo]()
  private val authInfoRepository = new DelegableAuthInfoRepository(authInfoDao)

  private val basicProvider = new BasicAuthProvider(authInfoRepository, passwordHasherRegistry)

  private val sut = new AdminUserService(passwordHasherRegistry, adminUserDao, authInfoRepository)

  "an admin service" can "add an admin user" in {
    await(sut.add(basicProvider, "username", "password"))

    val expectedLoginInfo = LoginInfo(basicProvider.id, "username")

    val actualPwdInfo = await(authInfoRepository.find[PasswordInfo](expectedLoginInfo)).get

    assert(bCryptHasher.matches(actualPwdInfo, "password"))
  }

  it can "retrieve an admin user" in {
    val loginInfo = LoginInfo(basicProvider.id, "username")

    (adminUserDao.withLoginInfo _).expects(loginInfo).returns(Future.successful(None))

    val actualResult = await(sut.retrieve(loginInfo))

    assert(actualResult === None)
  }

  it can "change a password" in {
    val loginInfo = LoginInfo(basicProvider.id, "password")
    val user = AdminUser("username", loginInfo)

    await(sut.changePassword(user, "password1"))

    val actualPwdInfo = await(authInfoRepository.find[PasswordInfo](loginInfo)).get

    assert(bCryptHasher.matches(actualPwdInfo, "password1"))
  }

  it can "remove an admin user" in {
    val loginInfo = LoginInfo(basicProvider.id, "password")
    val user = AdminUser("username", loginInfo)

    (adminUserDao.remove _).expects(user).returns(Future.successful(Unit))

    await(sut.remove(user))
  }

}
