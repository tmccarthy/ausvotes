package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.ausvotes.shared.aws.data.S3ObjectKey
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class EntityLocationsSpec extends ImprovedFlatSpec {

  "the groups object" should "have a location" in {
    assert(
      EntityLocations.locationOfGroupsObject(SenateElection.`2016`.electionForState(State.SA).get) ===
        S3ObjectKey("recountData", "2016", "SA", "groups.json")
    )
  }

  "the candidates object" should "have a location" in {
    assert(
      EntityLocations.locationOfCandidatesObject(SenateElection.`2016`.electionForState(State.SA).get) ===
        S3ObjectKey("recountData", "2016", "SA", "candidates.json")
    )
  }

  "the preference tree object" should "have a location" in {
    assert(
      EntityLocations.locationOfPreferenceTree(SenateElection.`2016`.electionForState(State.SA).get) ===
        S3ObjectKey("recountData", "2016", "SA", "preferences.tree")
    )
  }

  "the canonical recount object" should "have a location" in {
    assert(
      EntityLocations.locationOfCanonicalRecount(SenateElection.`2016`.electionForState(State.SA).get) ===
        S3ObjectKey("recountData", "2016", "SA", "canonicalRecountResult.json")
    )
  }

}
