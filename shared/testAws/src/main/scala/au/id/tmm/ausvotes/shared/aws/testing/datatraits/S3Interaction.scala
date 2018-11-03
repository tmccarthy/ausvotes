package au.id.tmm.ausvotes.shared.aws.testing.datatraits

import au.id.tmm.ausvotes.shared.aws.data.{ContentType, S3BucketName, S3ObjectKey}
import au.id.tmm.ausvotes.shared.aws.testing.datatraits.S3Interaction.InMemoryS3
import au.id.tmm.ausvotes.shared.aws.testing.datatraits.S3Interaction.InMemoryS3.{Bucket, S3Object}

trait S3Interaction[D] {
  def s3Content: InMemoryS3

  protected def copyWithS3Content(s3Content: InMemoryS3): D

  def writeString(bucketName: S3BucketName, objectKey: S3ObjectKey, content: String, contentType: ContentType): D =
    copyWithS3Content(
      s3Content.addObject(bucketName, InMemoryS3.S3Object(objectKey, content, contentType))
    )
}

object S3Interaction {

  final case class InMemoryS3(buckets: Set[InMemoryS3.Bucket]) {
    def apply(name: S3BucketName): Option[Bucket] = buckets.find(_.name == name)
    def hasBucket(name: S3BucketName): Boolean = buckets.exists(_.name == name)
    def addBucket(name: S3BucketName): InMemoryS3 = this.copy(buckets = buckets + Bucket(name, Set.empty))
    def addObject(bucketName: S3BucketName, s3Object: S3Object): InMemoryS3 = {
      val oldBucket = this(bucketName).get
      val newBucket = oldBucket.addObject(s3Object)

      this.copy(buckets - oldBucket + newBucket)
    }
  }

  object InMemoryS3 {
    val empty = InMemoryS3(Set.empty[InMemoryS3.Bucket])

    def apply(contents: ((S3BucketName, S3ObjectKey), (String, ContentType))*): InMemoryS3 = {
      val objectsPerBucket: Map[S3BucketName, Set[InMemoryS3.S3Object]] = contents.groupBy {
        case ((bucketName, _), _) => bucketName
      }.map {
        case (bucketName, recordsForBucket) =>
          bucketName -> recordsForBucket.map {
            case ((_, objectKey), (content, contentType)) => InMemoryS3.S3Object(objectKey, content, contentType)
          }.toSet
      }

      InMemoryS3(
        objectsPerBucket.map {
          case (bucketName, objects) => InMemoryS3.Bucket(bucketName, objects)
        }.toSet
      )
    }

    final case class Bucket(name: S3BucketName, objects: Set[S3Object]) {
      def apply(key: S3ObjectKey): Option[S3Object] = objects.find(_.key == key)
      def hasObject(key: S3ObjectKey): Boolean = objects.exists(_.key == key)
      def addObject(s3Object: S3Object): Bucket = this.copy(objects = objects + s3Object)
    }

    final case class S3Object(key: S3ObjectKey, content: String, contentType: ContentType)
  }

}
