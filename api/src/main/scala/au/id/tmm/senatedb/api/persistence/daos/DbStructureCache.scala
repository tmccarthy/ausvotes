package au.id.tmm.senatedb.api.persistence.daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.cache.SyncCacheApi
import scalikejdbc.DB
import scalikejdbc.interpolation.SQLSyntax

@ImplementedBy(classOf[ConcreteDbStructureCache])
trait DbStructureCache {
  def tableNames(): Set[String]

  def columnNamesFor(tableName: String): Set[String]

  def aliasedColumnNamesFor(tableNames: String*): Set[String] = {
    val aliasedColumnNames = for {
      tableName <- tableNames
      columnName <- columnNamesFor(tableName)
    } yield s"$tableName.$columnName"

    aliasedColumnNames.toSet
  }

  def columnListFor(tableNames: String*): SQLSyntax = {
    val aliasedColumnNames = aliasedColumnNamesFor(tableNames: _*)

    val columnList = aliasedColumnNames
      .map(aliasedColumnName => s"""$aliasedColumnName AS "$aliasedColumnName"""")
      .mkString(", ")

    SQLSyntax.createUnsafely(columnList)
  }
}

@Singleton
class ConcreteDbStructureCache @Inject() (cacheApi: SyncCacheApi) extends DbStructureCache {

  private val cacheName = "dbStructureCache"
  private val tableNameCacheKey = s"$cacheName.tableNames"
  private def columnNamesCacheKey(tableName: String) = s"$cacheName.columnNames.$tableName"

  override def tableNames(): Set[String] =
    cacheApi.getOrElseUpdate[Set[String]](tableNameCacheKey)(DB.getAllTableNames().toSet)

  override def columnNamesFor(tableName: String): Set[String] =
    cacheApi.getOrElseUpdate[Set[String]](columnNamesCacheKey(tableName))(DB.getColumnNames(tableName).toSet)
}