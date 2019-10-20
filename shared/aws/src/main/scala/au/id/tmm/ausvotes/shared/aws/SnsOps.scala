package au.id.tmm.ausvotes.shared.aws

import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.ziointerop._
import com.amazonaws.services.sns.{AmazonSNS, AmazonSNSClientBuilder}
import zio.IO

object SnsOps {

  private val snsClient: IO[Exception, AmazonSNS] = Sync[IO].syncException(AmazonSNSClientBuilder.defaultClient())

  // TODO topicArn should be a case class
  def putMessage(topicArn: String, messageBody: String): IO[Exception, Unit] = {
    for {
      client <- snsClient
      _ <- Sync[IO].syncException(client.publish(topicArn, messageBody))
    } yield ()
  }

}
