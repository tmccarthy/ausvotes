package au.id.tmm.senatedb.core.rawdata.download

import java.nio.file.Paths

import au.id.tmm.utilities.hashing.Digest
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DownloadUtilsSpec extends ImprovedFlatSpec {

  "the local resources check" should "fail if the digest doesn't match" in {
    val file = Paths.get(getClass.getResource("/au/id/tmm/senatedb/core/fixtures/firstPreferencesTest.csv").toURI)
    val badDigest = Digest("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")

    val result = DownloadUtils.localResourceIntegrityCheck(file, badDigest)

    assert(result.isFailure)
  }

}
