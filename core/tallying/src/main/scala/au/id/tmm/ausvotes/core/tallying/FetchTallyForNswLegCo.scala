package au.id.tmm.ausvotes.core.tallying

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.ausvotes.core.computations.{BallotFactsComputation, NswLegCoBallotWithFacts}
import au.id.tmm.ausvotes.data_sources.common.JsonCache
import au.id.tmm.ausvotes.data_sources.nswec.legco.{CountRules, NswLegCoData}
import au.id.tmm.ausvotes.model.nsw.legco._
import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.bfect.BMonad
import au.id.tmm.bfect.BMonad._
import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.effects.{Async, Bracket, Concurrent, Sync}
import au.id.tmm.bfect.fs2interop.Fs2Compiler
import au.id.tmm.countstv.counting.FullCountComputation
import au.id.tmm.countstv.model.CountParams
import au.id.tmm.countstv.model.preferences.PreferenceTree
import au.id.tmm.countstv.rules.RoundingRules
import fs2.Stream

class FetchTallyForNswLegCo[F[+_, +_]](
  implicit
  async: Async[F],
  bracket: Bracket[F],
  fs2Compiler: Fs2Compiler[F],
  concurrent: Concurrent[F],
  jsonCache: JsonCache[F],
  log: Log[F],
  nswLegCoData: NswLegCoData[F],
  fetchTally: FetchTally[F],
) {

  type FStream[+A] = Stream[F[Throwable, +?], A]

  def doFetch: FetchTallyForElection[F, NswLegCoElection, NswLegCoBallotWithFacts] =
    FetchTallyForElection(this.ballotStreamFor)

  private def ballotStreamFor(election: NswLegCoElection): FStream[NswLegCoBallotWithFacts] =
    Stream.eval {
      for {
        groupsAndCandidates <- nswLegCoData.fetchGroupsAndCandidatesFor(election)

        ballots = nswLegCoData.fetchPreferencesFor(election, groupsAndCandidates)

        ballotNormaliser = BallotNormaliser(CountRules.normalisationRulesFor(election), election, groupsAndCandidates.candidates)

        normalisedBallots = ballots.map(ballotNormaliser.normalise)

        preparedBallots = normalisedBallots.map(_.canonicalOrder.getOrElse(Vector.empty))

        preparedBallotsChunk <- preparedBallots.compile.toChunk

        preferenceTree <- Sync.syncException(PreferenceTree.fromIterator(groupsAndCandidates.candidates, numBallotsHint = 4000000)(preparedBallotsChunk.iterator))

        countParams = CountParams[Candidate](
          groupsAndCandidates.candidates,
          ineligibleCandidates = Set.empty,
          numVacancies = 21,
          roundingRules = RoundingRules.AEC,
        )
        completedCount <- BMonad.pureCatchException(FullCountComputation.runCount[Candidate](countParams, preferenceTree).anyOutcome)
        countData = CountData(election, completedCount)

        ballotsWithFactsStream = ballots.chunkN(5000)
          .parEvalMapUnordered(Runtime.getRuntime.availableProcessors() * 2) { chunk =>
            for {
              ballotsWithFacts <- Sync.syncException {
                BallotFactsComputation.computeFactsFor[NswLegCoElection, BallotJurisdiction, BallotId](
                  election,
                  Set.empty,
                  countData,
                  MatchingHowToVoteCalculator(Set.empty),
                  ballotNormaliser,
                  chunk.toVector,
                )
              }

              _ <- Log.logInfo("PROCESS_CHUNK", "election" -> election, "num_ballots" -> chunk.size)

            } yield Stream.emits(ballotsWithFacts)
          }
          .flatten

      } yield ballotsWithFactsStream
    }.flatten

}
