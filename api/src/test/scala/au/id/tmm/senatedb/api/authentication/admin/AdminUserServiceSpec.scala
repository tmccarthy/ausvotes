package au.id.tmm.senatedb.api.authentication.admin

import au.id.tmm.senatedb.api.persistence.daos.AdminUserDao
import au.id.tmm.utilities.concurrent.FutureUtils.await
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.BasicAuthProvider
import org.scalamock.scalatest.MockFactory

import scala.concurrent.Future

class AdminUserServiceSpec extends ImprovedFlatSpec with MockFactory {

  private val testLoginInfo = LoginInfo(BasicAuthProvider.ID, "asdf")
  private val testAdminUser = AdminUser("jane_doe", testLoginInfo)

  private val mockAdminUserDao = mock[AdminUserDao]

  private val sut = new AdminUserService(mockAdminUserDao)

  "the admin user service" should "retrieve an admin user" in {
    (mockAdminUserDao.withLoginInfo _)
      .expects(testLoginInfo)
      .returns(Future.successful(Some(testAdminUser)))

    val receivedAdminUser = await(sut.retrieve(testLoginInfo))

    assert(receivedAdminUser contains testAdminUser)
  }

}
