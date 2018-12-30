package au.id.tmm.ausvotes.core.rawdata

import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class AecResourceStoreSpec extends ImprovedFlatSpec with NeedsCleanDirectory {
  private val aecResourceStore = AecResourceStore.at(cleanDirectory)

  private val senateElection = SenateElection.`2016`
  private val federalElection = senateElection.federalElection
  private val state = State.NT
  private val election = SenateElectionForState(senateElection, state).right.get

  "the loading of distribution of preferences data" should "be successful" in {
    val source = aecResourceStore.distributionOfPreferencesFor(election)

    val numLines = source.get.size

    assert(numLines === 3579)
  }

  "the loading of raw first preferences data" should "download the data" in {
    val source = aecResourceStore.firstPreferencesFor(senateElection)

    val numLines = source.get.getLines().size

    assert(numLines === 839)
  }

  "the loading of raw formal preferences data" should "download the data" in {
    val source = aecResourceStore.formalPreferencesFor(election)

    val numLines = source.get.size

    assert(numLines === 6886936)
  }

  "the loading of raw polling places data" should "download the data" in {
    val source = aecResourceStore.pollingPlacesFor(federalElection)

    val numLines = source.get.getLines().size

    assert(numLines === 8330)
  }
}
