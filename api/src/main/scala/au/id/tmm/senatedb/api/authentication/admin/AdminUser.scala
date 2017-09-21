package au.id.tmm.senatedb.api.authentication.admin

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

final case class AdminUser(username: String,
                           loginInfo: LoginInfo,
                          ) extends Identity
