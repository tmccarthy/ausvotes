package au.id.tmm.senatedb.api.authentication.admin

import au.id.tmm.utilities.concurrent.FutureUtils.await
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.DummyAuthenticator
import play.api.test.FakeRequest

class AdminUserAuthenticatedSpec extends ImprovedFlatSpec {

  "the admin user authenticated condition" should "always be authenticated if there is an admin user" in {
    val loginInfo = LoginInfo("providerId", "username")
    val adminUser = AdminUser("username", loginInfo)
    val request = FakeRequest()

    val isAuthorised = await(AdminUserAuthenticated.isAuthorized(adminUser, DummyAuthenticator(loginInfo))(request))

    assert(isAuthorised === true)
  }

}
