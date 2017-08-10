package au.id.tmm.senatedb.api.integrationtest

import java.nio.file.Paths
import java.sql.DriverManager

import au.id.tmm.senatedb.api.integrationtest.PostgresService.PostgresReadyChecker
import com.whisk.docker.{DockerCommandExecutor, DockerContainer, DockerContainerState, DockerReadyChecker}
import org.flywaydb.core.Flyway
import org.scalatest._
import org.slf4j.LoggerFactory
import scalikejdbc.ConnectionPool
import scalikejdbc.config.DBs

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait PostgresService extends SpotifyClientDockerTestKit with TestSuite {
  private lazy val log = LoggerFactory.getLogger(this.getClass)

  val dbAdvertisedPort = 5432
  val dbExposedPort = 45454
  val dbUsername = "test_user"
  val dbPassword = "test_password"

  private val imageName = "tmccarthy/senatedb-integration-test-db"
  private val testDbImageId = {
    val dockerfileDir = Paths.get(getClass.getResource("/docker/database").toURI)

    client.build(dockerfileDir, imageName)
  }

  val testDbContainer: DockerContainer = DockerContainer(image = imageName)
    .withPorts(dbAdvertisedPort -> Some(dbExposedPort))
    .withEnv(s"POSTGRES_USER=$dbUsername", s"POSTGRES_PASSWORD=$dbPassword")
    .withReadyChecker(new PostgresReadyChecker(dbUsername, dbPassword, Some(dbExposedPort))
      .looped(15, 1.second)
    )

  abstract override def dockerContainers: List[DockerContainer] =
    testDbContainer :: super.dockerContainers

  override def beforeAll(): Unit = {
    super.beforeAll()

    DBs.setupAll()
  }

  override def afterAll(): Unit = {
    DBs.closeAll()

    super.afterAll()
  }

  override protected def withFixture(test: NoArgTest): Outcome = {
    totallyRefreshDatabase()
    super.withFixture(test)
  }

  private def totallyRefreshDatabase(): Unit = {
    val flyway = new Flyway()

    flyway.setDataSource(ConnectionPool().url, dbUsername, dbPassword)
    flyway.setLocations("db/migration/default")

    flyway.clean()

    flyway.migrate()
  }

  // Resolve conflict between the run methods in TestSuite and BeforeAndAfterAll
  abstract override def run(testName: Option[String], args: Args): Status =
    super[SpotifyClientDockerTestKit].run(testName, args)
}

object PostgresService {
  private class PostgresReadyChecker(user: String, password: String, port: Option[Int] = None)
    extends DockerReadyChecker {

    override def apply(container: DockerContainerState)(implicit docker: DockerCommandExecutor,
                                                        ec: ExecutionContext): Future[Boolean] =
      container
        .getPorts()
        .map { ports =>
          isReadyOnPort(port.getOrElse(ports.values.head))
        }

    //noinspection UnitInMap
    def isReadyOnPort(port: Int)(implicit docker: DockerCommandExecutor): Boolean = {
      Try {
        Class.forName("org.postgresql.Driver")
        val url = s"jdbc:postgresql://${docker.host}:$port/"
        Option(DriverManager.getConnection(url, user, password)).map(_.close).isDefined
      }.getOrElse(false)
    }
  }
}
