package au.id.tmm.senatedb.api.authentication.admin

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.DummyAuthenticator
import play.api.mvc.Request

import scala.concurrent.Future

case object AdminUserAuthenticated extends Authorization[AdminUser, DummyAuthenticator] {

  // Always authorised if there's an admin user
  override def isAuthorized[B](identity: AdminUser, authenticator: DummyAuthenticator)
                              (implicit request: Request[B]): Future[Boolean] = Future.successful(true)
}
