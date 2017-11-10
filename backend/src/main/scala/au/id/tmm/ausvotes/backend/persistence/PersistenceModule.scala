package au.id.tmm.ausvotes.backend.persistence

import java.util.concurrent.ForkJoinPool

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext

class PersistenceModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {}

  @Provides
  @Named("dbThreadParallelism")
  def dbThreadParallelism() = 5

  @Provides
  @Singleton
  @Named("dbExecutionContext")
  def dbExecutionContext(@Named("dbThreadParallelism") dbThreadParallelism: Int): ExecutionContext =
    ExecutionContext.fromExecutor(new ForkJoinPool(dbThreadParallelism))
}
