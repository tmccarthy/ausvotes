package au.id.tmm.senatedb.rawdata

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class AecResourceStoreSpec extends ImprovedFlatSpec with NeedsCleanDirectory {
  val aecResourceStore = AecResourceStore.at(cleanDirectory)

  "the loading of distribution of preferences data" should "be successful" in {
    val source = aecResourceStore.distributionOfPreferencesFor(SenateElection.`2016`, State.NT)

    val numLines = source.get.size

    assert(numLines === 3579)
  }

  "the loading of raw first preferences data" should "download the data" in {
    val source = aecResourceStore.firstPreferencesFor(SenateElection.`2016`)

    val numLines = source.get.getLines().size

    assert(numLines === 839)
  }

  "the loading of raw formal preferences data" should "download the data" in {
    val source = aecResourceStore.formalPreferencesFor(SenateElection.`2016`, State.NT)

    val numLines = source.get.size

    assert(numLines === 6886936)
  }

  "the loading of raw polling places data" should "download the data" in {
    val source = aecResourceStore.pollingPlacesFor(SenateElection.`2016`)

    val numLines = source.get.getLines().size

    assert(numLines === 8330)
  }
}
