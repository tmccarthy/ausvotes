package au.id.tmm.ausvotes.shared.aws

import java.net.URL

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class S3OpsSpec extends ImprovedFlatSpec {

  "an s3 object's URL" can "be built" in {
    val url = S3Ops.objectUrl(
      region = "ap-southeast-2",
      S3BucketName("recount-data.buckets.ausvotes.info"),
      S3ObjectKey("recountData", "2016", "NT", "candidates.json"),
    )

    val expectedUrl = new URL("https://s3-ap-southeast-2.amazonaws.com/recount-data.buckets.ausvotes.info/recountData/2016/NT/candidates.json")
    assert(url === expectedUrl)
  }

}
