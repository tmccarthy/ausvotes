package au.id.tmm.ausvotes.buildsrc

import com.amazonaws.services.apigateway.AmazonApiGatewayClient
import com.amazonaws.services.apigateway.model.CreateDeploymentRequest
import com.amazonaws.services.apigateway.model.CreateDeploymentResult
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.time.ZonedDateTime

class DeployApiTask extends DefaultTask {
    @Input
    Closure<String> restApiId

    @Input
    Closure<String> stageName

    @Input
    Closure<String> deploymentDescription = {
        "deployment_${ZonedDateTime.now().toString()}"
    }

    @TaskAction
    void doDeploy() {
        AmazonApiGatewayClient client = new AmazonApiGatewayClient()

        CreateDeploymentRequest deploymentRequest = new CreateDeploymentRequest()
                .withRestApiId(restApiId())
                .withStageName(stageName())
                .withDescription(deploymentDescription())

        CreateDeploymentResult result = client.createDeployment(deploymentRequest)
    }
}
