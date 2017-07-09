package au.id.tmm.senatedb.api

import java.nio.file.Paths

import au.id.tmm.senatedb.core.engine.{ParsedDataStore, TallyEngine}
import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.senatedb.core.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.senatedb.api.persistence.population.DbPopulationActor
import com.google.inject.{AbstractModule, Provides, Singleton}
import play.api.libs.concurrent.AkkaGuiceSupport
import scalikejdbc.{ConnectionPool, ConnectionPoolContext, MultipleConnectionPoolContext}

class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bindActor[DbPopulationActor]("dbPopulationActor")
  }

  @Provides
  def provideConnectionPoolContext(): ConnectionPoolContext =
    MultipleConnectionPoolContext(ConnectionPool.DEFAULT_NAME -> ConnectionPool())

  @Provides
  @Singleton
  def providePostcodeFlyweight: PostcodeFlyweight = PostcodeFlyweight()

  @Provides
  @Singleton
  def provideParsedDataStore(rawDataStore: RawDataStore): ParsedDataStore = ParsedDataStore(rawDataStore)

  @Provides
  @Singleton
  def provideRawDataStore(aecResourceStore: AecResourceStore): RawDataStore = RawDataStore(aecResourceStore)

  @Provides
  @Singleton
  def provideAecResourceStore: AecResourceStore = AecResourceStore.at(Paths.get("rawData"))

  @Provides
  def provideTallyEngine: TallyEngine = TallyEngine



}
