package au.id.tmm.senatedb.webapp

import com.google.inject.{AbstractModule, Provides}
import scalikejdbc.{ConnectionPool, ConnectionPoolContext, MultipleConnectionPoolContext}

class Module extends AbstractModule {

  override def configure(): Unit = {}

  @Provides
  def provideConnectionPoolContext(): ConnectionPoolContext =
    MultipleConnectionPoolContext(ConnectionPool.DEFAULT_NAME -> ConnectionPool())

  // TODO provide a singleton postcode flyweight

}
