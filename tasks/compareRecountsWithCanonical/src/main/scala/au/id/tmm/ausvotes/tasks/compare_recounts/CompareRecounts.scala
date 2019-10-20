package au.id.tmm.ausvotes.tasks.compare_recounts

import java.nio.file.{InvalidPathException, Path, Paths}

import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data.FetchSenateCountDataFromRaw
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.{FetchSenateCountData, FetchSenateGroupsAndCandidates}
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl.{AecResourceStore, FetchRawFederalElectionData}
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.instances.StateInstances.orderStatesByPopulation
import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.recountresources.RecountRequest
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree
import au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching.{GroupsAndCandidatesCache, PreferenceTreeCache}
import au.id.tmm.ausvotes.shared.recountresources.recount.RunRecount
import au.id.tmm.ausvotes.tasks.compare_recounts.CountComparison.Mismatch
import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.effects.extra.Console
import au.id.tmm.bfect.effects.{Now, Sync}
import au.id.tmm.bfect.ziointerop._
import au.id.tmm.countstv.model.countsteps._
import au.id.tmm.countstv.model.values.{Count, NumPapers, NumVotes, TransferValue}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateVoteCounts, CompletedCount, VoteCount}
import au.id.tmm.utilities.collection.CollectionUtils.Sortable
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import cats.implicits._
import org.apache.commons.lang3.exception.ExceptionUtils
import scalaz.zio
import zio.IO

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

      rawElection <- rawArgs.lift(2).toRight("Missing election").map(SenateElection.Id)
      election <- SenateElection.from(rawElection).toRight(s"Bad election $rawElection")
    } yield Args(aecResourcePath, s3BucketName, election)

  override def run(rawArgs: List[String]): IO[Nothing, Int] = {
    val errorOrSuccessCode = for {
      args <- IO.fromEither(argsFrom(rawArgs))
        .mapError(new RuntimeException(_))

      groupsAndCandidatesCache <- GroupsAndCandidatesCache(args.s3BucketName)
      preferenceTreeCache <- PreferenceTreeCache(groupsAndCandidatesCache)

      _ <- {
        implicit val aecResourceStore: AecResourceStore[IO] =
          AecResourceStore[IO](args.aecResourceStorePath, replaceExisting = false)

        implicit val fetchRawFederalElectionData: FetchRawFederalElectionData[IO] = FetchRawFederalElectionData(
          aecResourceStore.makeSourceForFederalPollingPlaceResource,
          aecResourceStore.makeSourceForFormalSenatePreferencesResource,
          aecResourceStore.makeSourceForSenateDistributionOfPreferencesResource,
          aecResourceStore.makeSourceForSenateFirstPreferencesResource,
        )

        implicit val fetchPreferenceTree: FetchPreferenceTree[IO] = preferenceTreeCache
        implicit val fetchGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO] = groupsAndCandidatesCache
        implicit val fetchCanonicalCountResult: FetchSenateCountData[IO] = FetchSenateCountDataFromRaw[IO]

        generalRun[IO](args.election)
      }
    } yield 0

    errorOrSuccessCode.catchAll { e =>
      val stackTrace = ExceptionUtils.getStackTrace(e)

      Console[IO].print(stackTrace).map(_ => 1)
    }
  }

  private def generalRun[F[+_, +_] : FetchSenateGroupsAndCandidates : FetchPreferenceTree : FetchSenateCountData : Sync : Log : Now : Console]
  (
    senateElection: SenateElection,
  ): F[Exception, Unit] = {
    val elections: Set[SenateElectionForState] = senateElection.allStateElections

    for {
      comparisons <- elections.toList
        .sortBy(_.state)(orderStatesByPopulation.reverse)
        .traverse((election: SenateElectionForState) =>
          compareFor[F](election)
        )

      _ <- comparisons.flatMap(RenderCountComparison.render).map(Console[F].println).sequence
    } yield ()
  }

  private def compareFor[F[+_, +_] : FetchPreferenceTree : FetchSenateCountData : FetchSenateGroupsAndCandidates : Sync : Log : Now]
  (
    election: SenateElectionForState,
  ): F[Exception, CountComparison] = {
    for {
      groupsAndCandidates <- FetchSenateGroupsAndCandidates.senateGroupsAndCandidatesFor(election): F[Exception, SenateGroupsAndCandidates]
      canonicalCount <- FetchSenateCountData.senateCountDataFor(election, groupsAndCandidates): F[Exception, SenateCountData]

      computedCountRequest = RecountRequest(
        election,
        canonicalCount.completedCount.countParams.numVacancies,
        canonicalCount.completedCount.outcomes.ineligibleCandidates.map(_.candidateDetails.id),
        doRounding = true,
      )

      computedCountPossibilities <- RunRecount.runRecountRequest(computedCountRequest)

    } yield findBestComparisonBetween(election)(canonicalCount.completedCount, computedCountPossibilities)
  }

  private def findBestComparisonBetween(election: SenateElectionForState)
                                       (
                                         canonicalCountResult: CompletedCount[SenateCandidate],
                                         computedCountResults: ProbabilityMeasure[CompletedCount[SenateCandidate]],
                                       ): CountComparison = {
    computedCountResults.asMap.keySet
      .map { computedCount =>
        val mismatches = compareRecounts(canonicalCountResult, computedCount)

        CountComparison(
          election,
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
                               canonicalCount: CompletedCount[SenateCandidate],
                               computedCount: CompletedCount[SenateCandidate],
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
      if (canonicalFinalVoteCounts.exhausted != computedFinalVoteCounts.exhausted) Some(Mismatch.FinalExhausted(canonicalFinalVoteCounts.exhausted, computedFinalVoteCounts.exhausted)) else None,
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
          val misallocationSummary = misallocatedBallotsBetween(allCandidates, canonicalCountStep.candidateVoteCounts, computedCountStep.candidateVoteCounts)

          if (misallocationSummary.totalMisallocation != VoteCount(0)) {
            Some(count -> misallocationSummary)
          } else {
            None
          }
        case (count, _, _) => None
      }.flatten

      mismatchesPerCount.headOption.map { case (firstBadCount, _) =>
        Mismatch.VoteCountAtCount(
          SortedMap(mismatchesPerCount: _*),
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

  private def actionOf(countStep: CountStep[SenateCandidate]): Mismatch.ActionAtCount.Action = countStep match {
    case InitialAllocation(_, _) => Mismatch.ActionAtCount.Action.InitialAllocation
    case AllocationAfterIneligibles(_, _, _) => Mismatch.ActionAtCount.Action.AllocationAfterIneligibles
    case DistributionCountStep(_, _, _, distributionSource) => Mismatch.ActionAtCount.Action.Distribution(distributionSource)
    case ExcludedNoVotesCountStep(_, _, _, excludedCandidate) => Mismatch.ActionAtCount.Action.ExcludedNoVotes(excludedCandidate)
    case ElectedNoSurplusCountStep(_, _, _, electedCandidate, sourceCounts) => Mismatch.ActionAtCount.Action.ElectedNoSurplus(electedCandidate, sourceCounts)
  }

  private def zip[U](left: CountSteps[SenateCandidate], right: CountSteps[SenateCandidate])(action: (Count, Option[CountStep[SenateCandidate]], Option[CountStep[SenateCandidate]]) => U): List[U] = {
    val largestCount = left.last.count max right.last.count

    Range.inclusive(0, largestCount.asInt).map { countAsInt =>
      val count = Count(countAsInt)
      val leftCountStep = left.lift(count)
      val rightCountStep = right.lift(count)

      action(count, leftCountStep, rightCountStep)
    }.toList
  }

  private def misallocatedBallotsBetween(
                                          allCandidates: Set[SenateCandidate],
                                          left: CandidateVoteCounts[SenateCandidate],
                                          right: CandidateVoteCounts[SenateCandidate],
                                        ): Mismatch.VoteCountAtCount.VoteCountMisallocationSummary = {
    val differencesForCandidates = allCandidates.toList.map { candidate =>
      val votesForCandidateLeft = left.perCandidate(candidate)
      val votesForCandidateRight = right.perCandidate(candidate)

      candidate -> (votesForCandidateRight - votesForCandidateLeft)
    }

    val (worstCandidate, worstCandidateDiff) = differencesForCandidates.maxBy { case (candidate, diff) => math.abs(diff.numVotes.asDouble) }

    val differenceForRoundingError = right.roundingError - left.roundingError
    val differenceForExhaustedVotes = right.exhausted - left.exhausted

    val totalMisallocation = (differencesForCandidates.map(_._2) :+ differenceForRoundingError :+ differenceForExhaustedVotes)
      .map { voteCount =>
        VoteCount(
          NumPapers(math.abs(voteCount.numPapers.asLong)),
          NumVotes(math.abs(voteCount.numVotes.asDouble)),
        )
      }
      .reduce(_ + _)

    Mismatch.VoteCountAtCount.VoteCountMisallocationSummary(
      totalMisallocation,
      worstCandidate,
      worstCandidateDiff,
      differenceForExhaustedVotes,
      differenceForRoundingError,
    )
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
                        left: DistributionCountStep.Source[SenateCandidate],
                        right: DistributionCountStep.Source[SenateCandidate],
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
