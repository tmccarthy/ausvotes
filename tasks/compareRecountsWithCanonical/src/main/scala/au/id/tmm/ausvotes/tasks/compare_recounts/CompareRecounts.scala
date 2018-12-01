package au.id.tmm.ausvotes.tasks.compare_recounts

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.shared.io.actions.{Log, Now}
import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.io.typeclasses.{Monad, Parallel, SyncEffects}
import au.id.tmm.ausvotes.shared.recountresources.RecountRequest
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree
import au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching.{GroupsAndCandidatesCache, PreferenceTreeCache}
import au.id.tmm.ausvotes.tasks.compare_recounts.CountComparison.Mismatch
import au.id.tmm.countstv.model.countsteps._
import au.id.tmm.countstv.model.values.{Count, NumPapers, NumVotes}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateVoteCounts, CompletedCount, VoteCount}
import au.id.tmm.utilities.collection.CollectionUtils.Sortable
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import scalaz.zio
import scalaz.zio.IO

import scala.Ordering.Implicits._

object CompareRecounts extends zio.App {

  override def run(args: List[String]): IO[Nothing, ExitStatus] = {
    val election = SenateElection.`2016` // TODO take from args
    val states = election.states

    for {
      groupsAndCandidatesCache <- GroupsAndCandidatesCache(???)
      preferenceTreeCache <- PreferenceTreeCache(groupsAndCandidatesCache)

      comparisons <- {
        implicit val fetchPreferenceTree: FetchPreferenceTree[IO] = preferenceTreeCache
        implicit val fetchCanonicalCountResult: FetchCanonicalCountResult[IO] = ???

        IO.traverse(states) { state =>
          compareFor[IO](election, state).map((election, state) -> _)
        }.map(_.toMap)
      }

    } yield comparisons
  }

  private def compareFor[F[+_, +_] : FetchPreferenceTree : FetchCanonicalCountResult : Parallel : SyncEffects : Log : Now : Monad]
  (
    election: SenateElection,
    state: State,
  ): F[Nothing, CountComparison] = {
    for {
      canonicalCount <- (???): F[Nothing, CompletedCount[Candidate]]

      computedCountRequest = RecountRequest(
        election,
        state,
        canonicalCount.numVacancies,
        canonicalCount.outcomes.ineligibleCandidates.map(_.aecId),
        doRounding = false,
      )

      computedCountPossibilities <- (???): F[Nothing, ProbabilityMeasure[CompletedCount[Candidate]]]

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
          mismatches.toSortedSet,
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

      if (canonicalAction != computedAction) {
        List(Mismatch.ActionAtCount(count, canonicalAction, computedAction))
      } else {
        Nil
      }
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
          nonZeroMismatchesPerCount.toMap,
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

}
