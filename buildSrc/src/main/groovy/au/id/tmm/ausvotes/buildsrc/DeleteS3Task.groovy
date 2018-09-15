package au.id.tmm.ausvotes.buildsrc

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.DeleteObjectsRequest
import com.amazonaws.services.s3.model.ObjectListing
import org.gradle.api.DefaultTask
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

        def deleteObjectsRequest = new DeleteObjectsRequest(bucket)
                .withKeys(*keys)

        client.deleteObjects(deleteObjectsRequest)
    }
}
