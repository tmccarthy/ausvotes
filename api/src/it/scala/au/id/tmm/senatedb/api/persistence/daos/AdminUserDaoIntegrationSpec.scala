package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.authentication.admin.AdminUser
import au.id.tmm.senatedb.api.integrationtest.PostgresService
import au.id.tmm.senatedb.api.services.exceptions.NoSuchAdminUserException
import au.id.tmm.utilities.concurrent.FutureUtils.await
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.BasicAuthProvider

class AdminUserDaoIntegrationSpec extends ImprovedFlatSpec with PostgresService {

  private val testLoginInfo = LoginInfo(BasicAuthProvider.ID, "asdf")
  private val testAdminUser = AdminUser("jane_doe", testLoginInfo)
  private val sut = new AdminUserDao(new LoginInfoDao())

  "the admin user dao" can "write an admin user and retrieve it" in {
    await(sut.write(testAdminUser))

    val returnedUser = await(sut.withLoginInfo(testLoginInfo))

    assert(returnedUser contains testAdminUser)
  }

  it should "return nothing when asked for an admin user that hasn't been stored" in {
    val returnedUser = await(sut.withLoginInfo(testLoginInfo))

    assert(returnedUser.isEmpty)
  }

  it should "throw when attempting to write an already saved admin user" in {
    await(sut.write(testAdminUser))

    intercept[IllegalStateException] {
      await(sut.write(testAdminUser))
    }
  }

  it can "delete an added admin user" in {
    val foundAdminUser = await(
      for {
        _ <- sut.write(testAdminUser)
        _ <- sut.remove(testAdminUser)
        foundAdminUser <- sut.withLoginInfo(testLoginInfo)
      } yield foundAdminUser
    )

    assert(foundAdminUser === None)
  }

  it should "throw if attempting to delete a missing admin user" in {
    intercept[NoSuchAdminUserException] {
      await(sut.remove(testAdminUser))
    }
  }

}
