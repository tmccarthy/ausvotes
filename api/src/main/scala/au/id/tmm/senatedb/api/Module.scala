package au.id.tmm.senatedb.api

import au.id.tmm.senatedb.api.persistence.PersistenceModule
import com.google.inject.{AbstractModule, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule
import scalikejdbc.{ConnectionPool, ConnectionPoolContext, MultipleConnectionPoolContext}

import scala.concurrent.ExecutionContext

class Module extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    install(new CoreModule())
    install(new PersistenceModule())
  }

  @Provides
  def provideConnectionPoolContext(): ConnectionPoolContext =
    MultipleConnectionPoolContext(ConnectionPool.DEFAULT_NAME -> ConnectionPool())

  @Provides
  @Singleton
  def globalExecutionContext(): ExecutionContext = ExecutionContext.global

}
