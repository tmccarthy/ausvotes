package au.id.tmm.ausvotes.backend.integrationtest

import com.spotify.docker.client.{DefaultDockerClient, DockerClient}
import com.whisk.docker.DockerFactory
import com.whisk.docker.impl.spotify.SpotifyDockerFactory
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.Suite

trait SpotifyClientDockerTestKit extends DockerTestKit { this: Suite =>

  val client: DockerClient = DefaultDockerClient.fromEnv().build()

  override implicit val dockerFactory: DockerFactory = new SpotifyDockerFactory(client)

}
