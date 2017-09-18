package au.id.tmm.senatedb.api.authentication

import javax.inject.Singleton

import au.id.tmm.senatedb.api.authentication.admin.AdminRestAuthEnvironment
import au.id.tmm.senatedb.api.persistence.daos.PasswordDao
import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.{Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext

class SilhouetteModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[Silhouette[AdminRestAuthEnvironment]].to[SilhouetteProvider[AdminRestAuthEnvironment]]
  }

  @Provides
  @Singleton
  def authInfoRepository(passwordDao: PasswordDao, ec: ExecutionContext): AuthInfoRepository =
    new DelegableAuthInfoRepository(passwordDao)(ec)

  @Provides
  def passwordHasherRegistry: PasswordHasherRegistry = PasswordHasherRegistry(current = new BCryptPasswordHasher())

}
