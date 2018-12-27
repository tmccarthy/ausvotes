package au.id.tmm.ausvotes.tasks.compare_recounts

import java.nio.file.{InvalidPathException, Path, Paths}

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.StateInstances.orderStatesByPopulation
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.core.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.actions.{Console, Log, Now}
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.{Parallel, SyncEffects, BifunctorMonadError => BME}
import au.id.tmm.ausvotes.shared.recountresources.RecountRequest
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.{FetchCanonicalCountResult, FetchGroupsAndCandidates, FetchPreferenceTree}
import au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching.{GroupsAndCandidatesCache, PreferenceTreeCache}
import au.id.tmm.ausvotes.shared.recountresources.entities.core_fetching.CanonicalRecountComputation
import au.id.tmm.ausvotes.shared.recountresources.recount.RunRecount
import au.id.tmm.ausvotes.tasks.compare_recounts.CountComparison.Mismatch
import au.id.tmm.countstv.model.countsteps._
import au.id.tmm.countstv.model.values.{Count, NumPapers, NumVotes, TransferValue}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateVoteCounts, CompletedCount, VoteCount}
import au.id.tmm.utilities.collection.CollectionUtils.Sortable
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import cats.Applicative
import cats.implicits._
import scalaz.zio
import scalaz.zio.IO

import scala.Ordering.Implicits._
import scala.collection.immutable.SortedMap

object CompareRecounts extends zio.App {

  final case class Args(aecResourceStorePath: Path, s3BucketName: S3BucketName, election: SenateElection)

  private def argsFrom(rawArgs: List[String]): Either[String, Args] =
    for {
      rawAecResourcePath <- rawArgs.lift(0).toRight("Missing aec resource path")
      aecResourcePath <- try Right(Paths.get(rawAecResourcePath)) catch {
        case e: InvalidPathException => Left(e.getMessage)
      }

      s3BucketName <- rawArgs.lift(1).toRight("Missing bucket name").map(S3BucketName)

      rawElection <- rawArgs.lift(2).toRight("Missing election")
      election <- SenateElection.forId(rawElection).toRight(s"Bad election $rawElection")
    } yield Args(aecResourcePath, s3BucketName, election)

  override def run(rawArgs: List[String]): IO[Nothing, ExitStatus] = {
    val errorOrSuccessCode = for {
      args <- IO.fromEither(argsFrom(rawArgs))

      groupsAndCandidatesCache <- GroupsAndCandidatesCache(args.s3BucketName)
      preferenceTreeCache <- PreferenceTreeCache(groupsAndCandidatesCache)

      _ <- {
        implicit val fetchPreferenceTree: FetchPreferenceTree[IO] = preferenceTreeCache
        implicit val fetchGroupsAndCandidates: FetchGroupsAndCandidates[IO] = groupsAndCandidatesCache
        implicit val fetchCanonicalCountResult: FetchCanonicalCountResult[IO] = {
          val aecResourceStore = AecResourceStore.at(args.aecResourceStorePath)
          val rawDataStore = RawDataStore(aecResourceStore)
          val parsedDataStore = ParsedDataStore(rawDataStore)

          new CanonicalRecountComputation(parsedDataStore, groupsAndCandidatesCache)
        }

        generalRun[IO](args.election)
      }
    } yield ExitStatus.ExitNow(0)

    errorOrSuccessCode.catchAll(_ => IO.point(ExitStatus.ExitNow(1)))
  }

  private def generalRun[F[+_, +_] : FetchGroupsAndCandidates : FetchPreferenceTree : FetchCanonicalCountResult : Parallel : SyncEffects : Log : Now : Console : BME]
  (
    election: SenateElection,
  ): F[Exception, Unit] = {
    val states: Set[State] = election.states

    implicit def applicative[E]: Applicative[F[E, +?]] = BME.bifunctorMonadErrorIsAMonadError[E, F]

    for {
      /*_*/
      comparisons <- states.toList
        .sorted(orderStatesByPopulation.reverse)
        .traverse((state: State) =>
          compareFor[F](election, state)
        )

      _ <- comparisons.flatMap(RenderCountComparison.render).map(Console.println[F]).sequence
      /*_*/
    } yield ()
  }

  private def compareFor[F[+_, +_] : FetchPreferenceTree : FetchCanonicalCountResult : Parallel : SyncEffects : Log : Now : BME]
  (
    election: SenateElection,
    state: State,
  ): F[Exception, CountComparison] = {
    for {
      canonicalCount <- FetchCanonicalCountResult.fetchCanonicalCountResultFor(election, state)

      computedCountRequest = RecountRequest(
        election,
        state,
        canonicalCount.countParams.numVacancies,
        canonicalCount.outcomes.ineligibleCandidates.map(_.aecId),
        doRounding = true,
      )

      computedCountPossibilities <- RunRecount.runRecountRequest(computedCountRequest)

    } yield findBestComparisonBetween(election, state)(canonicalCount, computedCountPossibilities)
  }

  private def findBestComparisonBetween(election: SenateElection, state: State)
                                       (
                                         canonicalCountResult: CompletedCount[Candidate],
                                         computedCountResults: ProbabilityMeasure[CompletedCount[Candidate]],
                                       ): CountComparison = {
    computedCountResults.asMap.keySet
      .map { computedCount =>
        val mismatches = compareRecounts(canonicalCountResult, computedCount)

        CountComparison(
          election,
          state,
          canonicalCountResult,
          computedCount,
          mismatches.collect { case m: Mismatch.CandidateStatusType => m }.toSortedSet,
          mismatches.collect { case m: Mismatch.CandidateStatus => m }.toSortedSet,
          mismatches.collect { case m: Mismatch.FinalRoundingError => m }.headOption,
          mismatches.collect { case m: Mismatch.FinalExhausted => m }.headOption,
          mismatches.collect { case m: Mismatch.ActionAtCount => m }.toSortedSet,
          mismatches.collect { case m: Mismatch.VoteCountAtCount => m }.toSortedSet,
        )
      }
      .minBy(_.mismatchSignificance)
  }

  private def compareRecounts(
                               canonicalCount: CompletedCount[Candidate],
                               computedCount: CompletedCount[Candidate],
                             ): Set[Mismatch] = {

    val canonicalFinalStatuses = canonicalCount.countSteps.last.candidateStatuses
    val canonicalFinalVoteCounts = canonicalCount.countSteps.last.candidateVoteCounts

    val computedFinalStatuses = computedCount.countSteps.last.candidateStatuses
    val computedFinalVoteCounts = computedCount.countSteps.last.candidateVoteCounts

    val allCandidates = canonicalFinalStatuses.allCandidates

    val statusMismatches: Set[Mismatch] = allCandidates.flatMap { candidate =>
      val canonicalFinalStatus = canonicalFinalStatuses.asMap(candidate)
      val computedFinalStatus = computedFinalStatuses.asMap(candidate)

      Set(
        if (sameType(canonicalFinalStatus, computedFinalStatus)) None else Some(Mismatch.CandidateStatusType(candidate, canonicalFinalStatus, computedFinalStatus)),
        if (canonicalFinalStatus == computedFinalStatus) None else Some(Mismatch.CandidateStatus(candidate, canonicalFinalStatus, computedFinalStatus)),
      )
    }.flatten

    val finalSpecialVotesMismatches: Set[Mismatch] = Set(
      if (canonicalFinalVoteCounts.roundingError != computedFinalVoteCounts.roundingError) Some(Mismatch.FinalRoundingError(canonicalFinalVoteCounts.roundingError, computedFinalVoteCounts.roundingError)) else None,
      if (canonicalFinalVoteCounts.exhausted != computedFinalVoteCounts.exhausted) Some(Mismatch.FinalRoundingError(canonicalFinalVoteCounts.exhausted, computedFinalVoteCounts.exhausted)) else None,
    ).flatten

    val actionMismatches: Set[Mismatch] = zip(canonicalCount.countSteps, computedCount.countSteps) { case (count, canonicalCountStep, computedCountStep) =>
      val canonicalAction = canonicalCountStep.map(actionOf)
      val computedAction = computedCountStep.map(actionOf)

      val actionsMatch = (canonicalAction, computedAction) match {
        case (Some(canonicalAction), Some(computedAction)) => compareActions(canonicalAction, computedAction)
        case (None, None) => true
        case (_, _) => false
      }

      if (actionsMatch) Nil else List(Mismatch.ActionAtCount(count, canonicalAction, computedAction))
    }.toSet.flatten

    val ballotAndVoteMismatches: Option[Mismatch] = {
      val mismatchesPerCount = zip(canonicalCount.countSteps, computedCount.countSteps) {
        case (count, Some(canonicalCountStep), Some(computedCountStep)) =>
          count -> numMisallocatedBallotsBetween(allCandidates, canonicalCountStep.candidateVoteCounts, computedCountStep.candidateVoteCounts)
        case (count, _, _) => count -> VoteCount.zero
      }

      val nonZeroMismatchesPerCount = mismatchesPerCount.dropWhile { case (count, numMismatches) => numMismatches == VoteCount.zero }

      nonZeroMismatchesPerCount.headOption.map { case (firstBadCount, _) =>
        Mismatch.VoteCountAtCount(
          SortedMap(nonZeroMismatchesPerCount: _*),
          firstBadCount,
          canonicalCount.countSteps(firstBadCount).candidateVoteCounts,
          computedCount.countSteps(firstBadCount).candidateVoteCounts,
        )
      }
    }

    statusMismatches ++ finalSpecialVotesMismatches ++ actionMismatches ++ ballotAndVoteMismatches
  }

  private def sameType(left: CandidateStatus, right: CandidateStatus): Boolean = (left, right) match {
    case (_: CandidateStatus.Elected, _: CandidateStatus.Elected) => true
    case (_: CandidateStatus.Excluded, _: CandidateStatus.Excluded) => true
    case (CandidateStatus.Ineligible, CandidateStatus.Ineligible) => true
    case (CandidateStatus.Remaining, CandidateStatus.Remaining) => true
    case (_, _) => false
  }

  private def actionOf(countStep: CountStep[Candidate]): Mismatch.ActionAtCount.Action = countStep match {
    case InitialAllocation(_, _) => Mismatch.ActionAtCount.Action.InitialAllocation
    case AllocationAfterIneligibles(_, _, _) => Mismatch.ActionAtCount.Action.AllocationAfterIneligibles
    case DistributionCountStep(_, _, _, distributionSource) => Mismatch.ActionAtCount.Action.Distribution(distributionSource)
    case ExcludedNoVotesCountStep(_, _, _, excludedCandidate) => Mismatch.ActionAtCount.Action.ExcludedNoVotes(excludedCandidate)
    case ElectedNoSurplusCountStep(_, _, _, electedCandidate, sourceCounts) => Mismatch.ActionAtCount.Action.ElectedNoSurplus(electedCandidate, sourceCounts)
  }

  private def zip[U](left: CountSteps[Candidate], right: CountSteps[Candidate])(action: (Count, Option[CountStep[Candidate]], Option[CountStep[Candidate]]) => U): List[U] = {
    val largestCount = left.last.count max right.last.count

    Range.inclusive(0, largestCount.asInt).map { countAsInt =>
      val count = Count(countAsInt)
      val leftCountStep = left.lift(count)
      val rightCountStep = right.lift(count)

      action(count, leftCountStep, rightCountStep)
    }.toList
  }

  private def numMisallocatedBallotsBetween(
                                             allCandidates: Set[Candidate],
                                             left: CandidateVoteCounts[Candidate],
                                             right: CandidateVoteCounts[Candidate],
                                           ): VoteCount = {
    val differencesForCandidates = allCandidates.toList.map { candidate =>
      val votesForCandidateLeft = left.perCandidate(candidate)
      val votesForCandidateRight = right.perCandidate(candidate)

      votesForCandidateRight - votesForCandidateLeft
    }

    val differenceForRoundingError = right.roundingError - left.roundingError
    val differenceForExhaustedVotes = right.exhausted - left.exhausted

    (differencesForCandidates :+ differenceForRoundingError :+ differenceForExhaustedVotes)
      .map { voteCount =>
        VoteCount(
          NumPapers(math.abs(voteCount.numPapers.asLong)),
          NumVotes(math.abs(voteCount.numVotes.asDouble)),
        )
      }
      .reduce(_ + _)
  }

  private def compareActions(left: Mismatch.ActionAtCount.Action, right: Mismatch.ActionAtCount.Action): Boolean = {
    import Mismatch.ActionAtCount.Action._

    // Source counts 0 and 1 are equivalent
    def compareSourceCounts(left: Set[Count], right: Set[Count]) =
      left.map {
        case Count(1) => Count(0)
        case c => c
      } == right.map {
        case Count(1) => Count(0)
        case c => c
      }

    def compareTransferValues(left: TransferValue, right: TransferValue) = math.abs(left.factor - right.factor) < 1e-6

    def compareSources(
                        left: DistributionCountStep.Source[Candidate],
                        right: DistributionCountStep.Source[Candidate],
                      ): Boolean =
      left.candidateDistributionReason == right.candidateDistributionReason &&
        left.candidate == right.candidate &&
        compareTransferValues(left.transferValue, right.transferValue) &&
        compareSourceCounts(left.sourceCounts, right.sourceCounts)

    (left, right) match {
      case (InitialAllocation, InitialAllocation) => true
      case (AllocationAfterIneligibles, AllocationAfterIneligibles) => true
      case (Distribution(leftSource), Distribution(rightSource)) => compareSources(leftSource, rightSource)
      case (ExcludedNoVotes(leftSource), ExcludedNoVotes(rightSource)) => leftSource == rightSource
      case (ElectedNoSurplus(leftSource, leftSourceCounts), ElectedNoSurplus(rightSource, rightSourceCounts)) =>
        leftSource == rightSource && compareSourceCounts(leftSourceCounts, rightSourceCounts)
      case (_, _) => false
    }
  }

}
