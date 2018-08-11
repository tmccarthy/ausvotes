package au.id.tmm.ausvotes.core.rawdata.download

import java.nio.file.Paths

import au.id.tmm.utilities.hashing.Digest
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DownloadUtilsSpec extends ImprovedFlatSpec {

  "the local resources check" should "fail if the digest doesn't match" in {
    val file = Paths.get(getClass.getResource("/au/id/tmm/ausvotes/core/rawdata/download/test_resource.txt").toURI)
    val badDigest = Digest("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")

    intercept[DataIntegrityException] {
      DownloadUtils.throwIfDigestMismatch(file, badDigest)
    }
  }

}
