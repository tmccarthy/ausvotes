package au.id.tmm.senatedb.api

import java.nio.file.Paths

import au.id.tmm.senatedb.core.engine.{ParsedDataStore, TallyEngine}
import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.senatedb.core.rawdata.{AecResourceStore, RawDataStore}
import com.google.inject.{AbstractModule, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule

/**
  * The module for wiring together the root classes from the "core" module
  */
class CoreModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {}

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
