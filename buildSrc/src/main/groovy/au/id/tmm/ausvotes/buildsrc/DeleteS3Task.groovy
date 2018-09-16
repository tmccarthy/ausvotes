package au.id.tmm.ausvotes.buildsrc

import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.DeleteObjectsRequest
import com.amazonaws.services.s3.model.ObjectListing
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class DeleteS3Task extends DefaultTask {

    @Input
    String bucket

    @Input
    String prefix

    @TaskAction
    void doDelete() {
        def client = AmazonS3ClientBuilder.defaultClient()

        ObjectListing objects = client.listObjects(bucket, prefix)

        List<String> keys = objects.objectSummaries.collect { it.key }

        if (!keys.empty) {
            logger.log(LogLevel.INFO, "Attempting to delete keys from s3: ${keys.toListString(100)}")

            def deleteObjectsRequest = new DeleteObjectsRequest(bucket)
                .withKeys(*keys)

            client.deleteObjects(deleteObjectsRequest)
        } else {
            logger.log(LogLevel.INFO, "No action as prefix $prefix does not match any objects")
        }
    }
}
