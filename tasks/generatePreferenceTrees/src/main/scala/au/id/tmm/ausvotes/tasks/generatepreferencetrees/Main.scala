package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import java.nio.file.{Files, Path}

import argonaut.CodecJson
import argonaut.JsonIdentity.ToJsonIdentity
import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.model.codecs.{CandidateCodec, GroupCodec, PartyCodec}
import au.id.tmm.ausvotes.core.model.parsing.{Ballot, Candidate, CandidatePosition}
import au.id.tmm.ausvotes.core.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.core.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.ausvotes.tasks.generatepreferencetrees.Logging.IoOps
import au.id.tmm.countstv.model.preferences.{PreferenceTree, PreferenceTreeSerialisation}
import au.id.tmm.utilities.geo.australia.State
import org.apache.commons.io.FileUtils
import scalaz.zio.{App, IO, console}

object Main extends App {

  override def run(args: List[String]): IO[Nothing, Main.ExitStatus] =
    applicationLogic(args)
      .timedLog("APP_RUN")
      .attempt.map(_.fold(_ => 1, _ => 0))
      .map(ExitStatus.ExitNow(_))

  private def applicationLogic(rawArgs: List[String]): IO[Exception, Unit] = {
    for {
      args <- IO.fromEither(Args.from(rawArgs))
      election = args.election
      parsedDataStore = ParsedDataStore(RawDataStore(AecResourceStore.at(args.dataStorePath)))

      computationInputs <- groupsAndCandidatesLogic(election, parsedDataStore) par divisionsAndPollingPlacesLogic(election, parsedDataStore)

      (groupsAndCandidates, divisionsAndPollingPlaces) = computationInputs

      outputPath <- IO.syncException(Files.createTempDirectory("recount_data"))

      _ <- IO.parTraverse(election.states)(writeOutputsFor(parsedDataStore, outputPath, election, _, groupsAndCandidates, divisionsAndPollingPlaces))

      _ <- console.putStrLn(s"$outputPath")

    } yield (groupsAndCandidates, divisionsAndPollingPlaces)
  }

  private def writeOutputsFor(
                               parsedDataStore: ParsedDataStore,
                               outputPath: Path,

                               election: SenateElection,
                               state: State,
                               groupsAndCandidates: GroupsAndCandidates,
                               divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                             ): IO[Exception, Unit] = {
    IO.syncException(parsedDataStore.ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces, state))
      .bracket(ballotsIterator => IO.sync(ballotsIterator.close())) { ballots =>
        outputsFor(election, state, groupsAndCandidates, divisionsAndPollingPlaces, ballots).flatMap { outputsToWrite =>
          writeOutputs(outputPath, outputsToWrite)
        }
      }
      .timedLog("WRITE_OUTPUTS", "State" -> state)
  }


  private def outputsFor(
                          election: SenateElection,
                          state: State,
                          groupsAndCandidates: GroupsAndCandidates,
                          divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                          ballots: Iterator[Ballot],
                        ): IO[Exception, OutputsForElection] = {
    val relevantGroupsAndCandidates = groupsAndCandidates.findFor(election, state)
    val relevantDivisionsAndPollingPlaces = divisionsAndPollingPlaces.findFor(election, state)

    val candidates = relevantGroupsAndCandidates.candidates

    val ballotNormaliser = BallotNormaliser(election, state, candidates)

    val numPapersHint = state match {
      case State.NSW => 4705270
      case State.VIC => 3653736
      case State.QLD => 2818997
      case State.WA  => 1413553
      case State.SA  => 1097710
      case State.TAS => 351380
      case State.ACT => 282045
      case State.NT  => 105539
    }

    val preparedBallots = ballots.map(ballotNormaliser.normalise(_).canonicalOrder).toIterable

    IO.syncException {
      PreferenceTree.from(candidates.map(_.btlPosition), numPapersHint)(preparedBallots)
    }.map { preferenceTree =>
      OutputsForElection(election, state, relevantGroupsAndCandidates, relevantDivisionsAndPollingPlaces, preferenceTree)
    }
  }

  private def writeOutputs(outputPath: Path, outputsToWrite: OutputsForElection): IO[Exception, Unit] = {
    val outputDirectoryForThisWrite = outputPath
      .resolve(outputsToWrite.election.id)
      .resolve(outputsToWrite.state.abbreviation)

    val preferenceTreeFile = outputDirectoryForThisWrite.resolve("preferences.tree")
    val groupsFile = outputDirectoryForThisWrite.resolve("groups.json")
    val candidatesFile = outputDirectoryForThisWrite.resolve("candidates.json")

    val writePreferenceTree = IO
      .syncException(Files.newOutputStream(preferenceTreeFile))
      .bracket(s => IO.sync(s.close())) { outputStream =>
        IO.syncException(PreferenceTreeSerialisation.serialise[CandidatePosition](outputsToWrite.preferenceTree, outputStream))
      }

    implicit val partyCodec: PartyCodec = PartyCodec()
    implicit val groupCodec: GroupCodec = GroupCodec()
    implicit val candidateCodec: CodecJson[Candidate] = CandidateCodec(outputsToWrite.groupsAndCandidates.groups)

    val writeGroupsFile = IO.syncException {
      val groupsInOrder = outputsToWrite.groupsAndCandidates.groups.toList.sortBy(_.index)
      val groupsJson = groupsInOrder.asJson.toString

      Files.write(groupsFile, java.util.Collections.singleton(groupsJson))
    }

    val writeCandidatesFile = IO.syncException {
      val candidatesInOrder = outputsToWrite.groupsAndCandidates.candidates.toList.sortBy(_.btlPosition)

      val candidatesJson = candidatesInOrder.asJson.toString

      Files.write(candidatesFile, java.util.Collections.singleton(candidatesJson))
    }

    for {
      _ <- IO.syncException(FileUtils.deleteDirectory(outputDirectoryForThisWrite.toFile))
      _ <- IO.syncException(Files.createDirectories(outputDirectoryForThisWrite))

      _ <- writePreferenceTree
      _ <- writeGroupsFile par writeCandidatesFile
    } yield Unit

  }

  private case class OutputsForElection(
                                         election: SenateElection,
                                         state: State,
                                         groupsAndCandidates: GroupsAndCandidates,
                                         divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                         preferenceTree: PreferenceTree.RootPreferenceTree[CandidatePosition],
                                       )

  private def groupsAndCandidatesLogic(election: SenateElection, parsedDataStore: ParsedDataStore): IO[Exception, GroupsAndCandidates] = {
    IO.syncException(parsedDataStore.groupsAndCandidatesFor(election))
  }

  private def divisionsAndPollingPlacesLogic(election: SenateElection, parsedDataStore: ParsedDataStore): IO[Exception, DivisionsAndPollingPlaces] = {
    IO.syncException(parsedDataStore.divisionsAndPollingPlacesFor(election))
  }

}
