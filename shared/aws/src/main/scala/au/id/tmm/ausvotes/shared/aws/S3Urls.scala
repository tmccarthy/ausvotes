package au.id.tmm.ausvotes.shared.aws

import java.net.URL

object S3Urls {

  def objectUrl(region: String, bucketName: S3BucketName, objectKey: S3ObjectKey): URL =
    new URL(s"https://s3-$region.amazonaws.com/${bucketName.asString}/${objectKey.asString}")


}
