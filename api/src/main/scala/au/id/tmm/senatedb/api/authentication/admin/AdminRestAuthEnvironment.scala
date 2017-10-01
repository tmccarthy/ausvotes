package au.id.tmm.senatedb.api.authentication.admin

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.DummyAuthenticator

class AdminRestAuthEnvironment extends Env {
  type I = AdminUser
  type A = DummyAuthenticator
}
