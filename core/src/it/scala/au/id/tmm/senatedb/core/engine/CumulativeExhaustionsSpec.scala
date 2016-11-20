package au.id.tmm.senatedb.core.engine

import au.id.tmm.senatedb.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.core.computations.exhaustion.ExhaustionCalculator
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.computation.BallotExhaustion.Exhausted
import au.id.tmm.senatedb.core.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

import scala.collection.mutable

class CumulativeExhaustionsSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  "the number of exhausted ballots" should "match up with the AEC data at every count" in {
    val aecRawDataStore = AecResourceStore.at(cleanDirectory)
    val rawDataStore = RawDataStore(aecRawDataStore)
    val parsedDataStore = ParsedDataStore(rawDataStore)

    val election = SenateElection.`2016`
    val state = State.TAS

    val groupsAndCandidates = parsedDataStore.groupsAndCandidatesFor(election)
    val divisionsAndPollingPlaces = parsedDataStore.divisionsAndPollingPlacesFor(election)

    val countData = parsedDataStore.countDataFor(election, groupsAndCandidates, state)

    val counts = 1 to countData.steps.last.count inclusive

    val actualCumulativeExhaustedVotesPerCount = {

      val allBallots = parsedDataStore.ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces, state).toVector

      val normaliser = BallotNormaliser(election, state, groupsAndCandidates.candidates)

      val ballotsWithNormalised = allBallots.map(ballot => ballot -> normaliser.normalise(ballot))

      val ballotsWithExhaustions = ExhaustionCalculator.exhaustionsOf(countData, ballotsWithNormalised)

      val actualExhaustedVotesPerCount = mutable.Map[Int, Double]() withDefaultValue 0d

      ballotsWithExhaustions.foreach {
        case (ballot, Exhausted(count, value, _)) => {
          val exhaustedSoFar = actualExhaustedVotesPerCount(count)

          actualExhaustedVotesPerCount.put(count, exhaustedSoFar + value)
        }
        case _ => {}
      }

      counts.map { count =>
        val countsUpToNow = 1 to count inclusive

        val exhausedToNow = countsUpToNow.map(actualExhaustedVotesPerCount).foldLeft(0d)(_ + _)

        count -> exhausedToNow
      }.toMap
    }

    val expectedCumulativeExhaustedVotesPerCount = countData.steps.map { step =>
      step.count -> step.exhaustedTransfer.votesTotal
    }.toMap

    counts.foreach(count => {
      val expected = expectedCumulativeExhaustedVotesPerCount(count)
      val actual = actualCumulativeExhaustedVotesPerCount(count)

      val absoluteError = actual - expected

      val fractionalError = if (actual != 0) {
        absoluteError / actual
      } else if (absoluteError > 0d) {
        Double.PositiveInfinity
      } else if (absoluteError < 0d) {
        Double.NegativeInfinity
      } else {
        0d
      }

      // The error is due to rounding in the AEC count data, so it is expected to increase slightly with every count.
      val errorIsAcceptable = math.abs(fractionalError) < 0.004 || math.abs(absoluteError) < 0.1d * count

      assert(errorIsAcceptable,
        s"For count $count, expected $expected got $actual. Abs error $absoluteError, fractionalError $fractionalError")
    })
  }
}
