package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.shared.aws.S3ObjectKey
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountLocationsSpec extends ImprovedFlatSpec {

  "The recount location" can "be computed" in {
    val recountLocation = RecountLocations.locationOfRecountFor(RecountRequest(
      SenateElection.`2016`,
      State.VIC,
      12,
      Set("123,456"),
    ))

    assert(recountLocation === S3ObjectKey("recounts") / "658da71678652bf42ed3491774817b319391c0e579e63606d2917ac7060f3936.json")
  }

}
