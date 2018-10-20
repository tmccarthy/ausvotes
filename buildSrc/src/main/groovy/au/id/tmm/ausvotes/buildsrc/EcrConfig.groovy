package au.id.tmm.ausvotes.buildsrc

import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage
import com.bmuschko.gradle.docker.tasks.image.DockerTagImage
import org.gradle.api.Project
import org.gradle.api.Task

class EcrConfig {

    static void addEcrTasks(
        Project target,
        String awsAccountId,
        String awsRegion,
        List<DockerBuildImage> buildImageTasks
    ) {
        target.repositories.jcenter()

        String ecr = "${awsAccountId}.dkr.ecr.${awsRegion}.amazonaws.com"

        Task pushAllImagesToEcr = target.tasks.create("pushAllImagesToEcr")

        for (DockerBuildImage buildImageTask : buildImageTasks) {
            String imageTasksId = buildImageTask.project.name.capitalize()

            def (String imageName, String tag) = buildImageTask.tag.split(':')

            def repositoryForAwsTag = "$ecr/$imageName"

            DockerTagImage tagImageForAwsTask = target.tasks.create("tag${imageTasksId}ImageForEcr", DockerTagImage.class)

            tagImageForAwsTask.dependsOn(buildImageTask)
            tagImageForAwsTask.imageId = buildImageTask.tag
            tagImageForAwsTask.repository = repositoryForAwsTag
            tagImageForAwsTask.tag = tag

            DockerPushImage pushImageToEcrTask = target.tasks.create("push${imageTasksId}ImageToEcr", DockerPushImage.class)

            pushImageToEcrTask.dependsOn(tagImageForAwsTask)
            pushImageToEcrTask.imageName = repositoryForAwsTag
            pushImageToEcrTask.tag = tag

            pushAllImagesToEcr.dependsOn(pushImageToEcrTask)
        }

        target.apply plugin: 'com.patdouble.awsecr'
        target.apply plugin: 'com.bmuschko.docker-remote-api'

        target.docker {
            registryCredentials {
                url = "https://$ecr"
            }
        }
    }

}
