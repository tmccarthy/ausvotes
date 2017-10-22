package au.id.tmm.ausvotes.api

import au.id.tmm.ausvotes.backend.BackendModule
import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext

class ApiModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    install(new BackendModule())
  }

  @Provides
  @Named("servletExecutionContext")
  @Singleton
  def servletExecutionContext: ExecutionContext = ExecutionContext.global

}
