package au.id.tmm.ausvotes.tasks.uploadpreferencetree

import java.nio.file.{Files, Path, Paths}

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.CandidatePosition
import au.id.tmm.ausvotes.core.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.countstv.model.preferences.PreferenceTree.RootPreferenceTree
import au.id.tmm.countstv.model.preferences.{PreferenceTree, PreferenceTreeSerialisation}
import au.id.tmm.utilities.geo.australia.State

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object Main {

  private val preferenceTreesPath = Files.createTempDirectory(Paths.get("/tmp"), "preference_trees")

  def main(args: Array[String]): Unit = {

    val parsedArgs: Args = Args(
      dataStorePath = Paths.get(args(0)),
      election = SenateElection.forId(args(1)).getOrElse(throw new IllegalArgumentException(s"Unrecognised election ${args(1)}")),
    )

    val parsedDataStore = ParsedDataStore(RawDataStore(AecResourceStore.at(parsedArgs.dataStorePath)))

    val election = parsedArgs.election

    val eventualGroupsAndCandidates = Future(parsedDataStore.groupsAndCandidatesFor(election))
    val eventualDivisionsAndPollingPlaces = Future(parsedDataStore.divisionsAndPollingPlacesFor(election))

    val writeFuture = for {
      groupsAndCandidates <- eventualGroupsAndCandidates
      divisionsAndPollingPlaces <- eventualDivisionsAndPollingPlaces
    } yield {
      election.states.foreach { state =>

        val candidates = groupsAndCandidates.candidates.filter(c => c.election == election && c.state == state)
        val ballotNormaliser = BallotNormaliser(election, state, candidates)

        resource.managed(parsedDataStore.ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces, state)).foreach { ballots =>
          val normalisedBallots = ballots.map(ballotNormaliser.normalise(_).canonicalOrder).toIterable

          val preferenceTree = PreferenceTree.from(candidates.map(_.btlPosition), numBallotsHint(state))(normalisedBallots)

          writeToS3(state, election, preferenceTree)
        }
      }
    }

    Await.result(writeFuture, Duration.Inf)
  }

  // TODO make this real
  private def writeToS3(state: State, election: SenateElection, preferenceTree: RootPreferenceTree[CandidatePosition]): Unit = {
    val destination = preferenceTreesPath.resolve(election.id).resolve(s"${state.abbreviation}.tree")

    Files.createDirectories(destination.getParent)

    println(s"Writing preference tree for $election $state to $destination")

    resource.managed(Files.newOutputStream(destination)).foreach { out =>
      PreferenceTreeSerialisation.serialise[CandidatePosition](preferenceTree, out)
    }

    println("Done")
  }

  private final case class Args(
                                 dataStorePath: Path,
                                 election: SenateElection,
                               )

  private def numBallotsHint(state: State): Int = state match {
    case State.NSW => 4705270
    case State.VIC => 3653736
    case State.QLD => 2818997
    case State.WA  => 1413553
    case State.SA  => 1097710
    case State.TAS => 351380
    case State.ACT => 282045
    case State.NT  => 105539
  }
}
