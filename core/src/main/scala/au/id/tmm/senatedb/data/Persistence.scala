package au.id.tmm.senatedb.data

import java.nio.file.Path

import au.id.tmm.senatedb.data.Persistence.{FormalPreferences, GroupsAndCandidates, Loadable}
import au.id.tmm.senatedb.data.database._
import au.id.tmm.senatedb.data.download.{LoadingFirstPreferences, LoadingFormalPreferences}
import au.id.tmm.senatedb.data.entityconstruction.{BallotWithPreferences, parseFirstPreferencesCsv, parseFormalPreferencesCsv}
import au.id.tmm.senatedb.model.{SenateElection, State}
import slick.driver.{H2Driver, JdbcDriver, SQLiteDriver}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

// TODO version changes in data model
class Persistence private (private[data] val localDataStore: Path,
                           private[data] val dal: DAL,
                           private[data] val database: Database)
                          (implicit executionContext: ExecutionContext) extends AutoCloseable {
  def initialiseIfNeeded(): Future[Unit] = {
    isInitialised.flatMap(alreadyInitialised => if (!alreadyInitialised) initialise() else Future.successful(Unit))
  }

  private def isInitialised: Future[Boolean] = {
    database.run(dal.isInitialised)
  }

  def initialise(): Future[Unit] = {
    database.run(dal.initialise())
  }

  def destroyIfNeeded(): Future[Unit] = {
    isInitialised.flatMap(alreadyInitialised => if (alreadyInitialised) destroy() else Future.successful(Unit))
  }

  def destroy(): Future[Unit] = {
    database.run(dal.destroy())
  }

  type StateAtElection = (SenateElection, State)

  // TODO return information about which rows didn't load successfully
  def load(loadables: Set[Loadable]): Future[Unit] = {
    for {
      _ <- loadGroupsAndCandidatesFor(loadables)
      _ <- loadBallotsFor(loadables)
    } yield ()
  }

  def loadGroupsAndCandidatesFor(loadables: Set[Loadable]): Future[Unit] = {
    val electionsToLoadCandidatesAndGroupsFor = loadables.collect { case GroupsAndCandidates(election) => election }

    val groupAndCandidateLoadFutures = electionsToLoadCandidatesAndGroupsFor
      .map(loadCandidatesAndGroupsFor)

    Future.sequence(groupAndCandidateLoadFutures).map(_ => Unit)
  }

  private def loadCandidatesAndGroupsFor(election: SenateElection): Future[Unit] = {
    val groupsAndRowsToInsert = for {
      csvLines <- LoadingFirstPreferences.csvLinesOf(localDataStore, election)
      (groups, candidates) <- {
        try {
          parseFirstPreferencesCsv(election, csvLines)
        } finally {
          csvLines.close()
        }
      }
    } yield (groups, candidates)

    groupsAndRowsToInsert match {
      case Success((groups, candidates)) => insertGroupsAndCandidates(groups, candidates)
      case Failure(e) => Future.failed(e)
    }
  }

  private def insertGroupsAndCandidates(groups: Set[GroupsRow], candidates: Set[CandidatesRow]): Future[Unit] = for {
    _ <- database.run(dal.insertGroups(groups))
    _ <- database.run(dal.insertCandidates(candidates))
  } yield Unit

  private def loadBallotsFor(loadables: Set[Loadable]): Future[Unit] = {
    val statesAtElectionsToLoadBallotsFor = loadables.collect {
      case FormalPreferences(election, state) => (election, state)
    }

    val ballotLoadFuture = statesAtElectionsToLoadBallotsFor
      .map(electionAndState => loadBallotsFor(electionAndState._1, electionAndState._2))

    Future.sequence(ballotLoadFuture).map(_ => Unit)
  }

  private def loadBallotsFor(election: SenateElection, state: State): Future[Unit] = {
    relevantCandidatesFor(election, state)
      .flatMap(failIfNoCandidates(election, state, _))
      .flatMap(relevantCandidates => loadBallotsFor(election, state, relevantCandidates))
  }

  private def relevantCandidatesFor(election: SenateElection, state: State): Future[Set[CandidatesRow]] = {
    import dal.driver.api._

    database.run(dal.candidatesForElectionInState(election, state).result)
      .map(_.toSet)
  }

  private def failIfNoCandidates(election: SenateElection, state: State, candidates: Set[CandidatesRow]): Future[Set[CandidatesRow]] = {
    if (candidates.isEmpty) {
      Future.failed(new IllegalStateException(s"Candidates haven't been loaded for $state at $election"))
    } else {
      Future(candidates)
    }
  }

  private def loadBallotsFor(election: SenateElection, state: State, candidates: Set[CandidatesRow]): Future[Unit] = {

    val csvLines = LoadingFormalPreferences.csvLinesOf(localDataStore, election, state)

    val ballotsAndPreferencesForInsertion = csvLines.flatMap(parseFormalPreferencesCsv(election, state, candidates, _))

    val loadFuture = ballotsAndPreferencesForInsertion match {
      case Success(toInsert) => insertBallotsAndPreferences(toInsert)
      case Failure(e) => Future.failed(e)
    }

    loadFuture.onComplete(_ => csvLines.foreach(_.close()))

    loadFuture
  }

  private def insertBallotsAndPreferences(ballotsAndPreferences: Iterator[Try[BallotWithPreferences]]): Future[Unit] = {
    val chunkInsertionFutures = ballotsAndPreferences
      .grouped(Persistence.INSERTION_CHUNK_SIZE)
      .map(insertBallotsAndPreferencesChunk)

    Future.sequence(chunkInsertionFutures).map(_ => Unit)
  }

  private def insertBallotsAndPreferencesChunk(triedBallotsWithPreferences: Iterable[Try[BallotWithPreferences]]): Future[Unit] = {
    val afterEvaluation = triedBallotsWithPreferences.toVector

    if (afterEvaluation.exists(_.isFailure)) {
      val firstFailure = afterEvaluation.find(_.isFailure)
      Future.failed(firstFailure.get.failed.get)

    } else {
      val allSuccesses = afterEvaluation.map(_.get)
      insertBallotsAndPreferences(allSuccesses)

    }
  }

  private def insertBallotsAndPreferences(ballotsWithPreferences: Iterable[BallotWithPreferences]): Future[Unit] = {
    import dal.driver.api._

    val insertAction = DBIO.seq(
      dal.insertBallots(ballotsWithPreferences.map(_.ballot)),
      dal.insertAtlPreferences(ballotsWithPreferences.flatMap(_.atlPreferences)),
      dal.insertBtlPreferences(ballotsWithPreferences.flatMap(_.btlPreferences))
    )

    database.run(insertAction)
  }

  def hasLoaded(loadable: Loadable): Future[Boolean] = ???

  def remove(loadables: Loadable): Future[Unit] = ???

  override def close(): Unit = database.close()

}

object Persistence {

  private val INSERTION_CHUNK_SIZE = 100

  def apply(dbPlatform: DbPlatform, localDataStore: Path)(implicit executionContext: ExecutionContext) : Persistence = {
    val dal = new DAL(dbPlatform.slickDriver)

    Class.forName(dbPlatform.jdbcDriverClassName)
    val database = dbPlatform match {
      case InMemoryH2(name) => Database.forURL(s"jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1")
      case _ => throw new UnsupportedOperationException(s"Unsupported $dbPlatform")
    }

    new Persistence(localDataStore, dal, database)
  }


  sealed abstract class DbPlatform(val slickDriver: JdbcDriver, val jdbcDriverClassName: String)
  case class InMemoryH2(name: String) extends DbPlatform(H2Driver, "org.h2.Driver")
  case class SQLite(location: Path) extends DbPlatform(SQLiteDriver, "org.sqlite.JDBC")

  sealed trait Loadable

  case class GroupsAndCandidates(election: SenateElection) extends Loadable
  case class FormalPreferences(election: SenateElection, state: State) extends Loadable
}