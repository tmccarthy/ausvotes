package au.id.tmm.senatedb.data.database

import java.io.Closeable
import java.nio.file.Path

import slick.driver.{H2Driver, JdbcDriver, SQLiteDriver}
import slick.jdbc.JdbcBackend._

import scala.concurrent.{ExecutionContext, Future}

class Persistence private (private[data] val dal: DAL, private[data] val database: Database)
                          (implicit executionContext: ExecutionContext)
  extends Closeable
    with PersistenceLifecycle
    with StoresGroupsAndCandidates
    with StoresBallots {

  implicit val ec = executionContext // Needed so the traits can use the executionContext

  import dal.driver.api._

  def runQuery[T, R](query: Query[T, R, Seq]): Future[Seq[R]] = execute(query.result)

  def execute[R](dbio: DBIO[R]): Future[R] = database.run(dbio)

  override def close(): Unit = database.close()
}

object Persistence {
  def apply(dbPlatform: DbPlatform)(implicit executionContext: ExecutionContext) : Persistence = {
    val dal = new DAL(dbPlatform.slickDriver)

    Class.forName(dbPlatform.jdbcDriverClassName)
    val database = dbPlatform match {
      case InMemoryH2(name) => Database.forURL(s"jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1")
      case _ => throw new UnsupportedOperationException(s"Unsupported $dbPlatform")
    }

    new Persistence(dal, database)
  }

  sealed abstract class DbPlatform(val slickDriver: JdbcDriver, val jdbcDriverClassName: String)
  case class InMemoryH2(name: String) extends DbPlatform(H2Driver, "org.h2.Driver")
  case class SQLite(location: Path) extends DbPlatform(SQLiteDriver, "org.sqlite.JDBC")
}
