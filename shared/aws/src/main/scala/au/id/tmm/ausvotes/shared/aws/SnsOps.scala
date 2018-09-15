package au.id.tmm.ausvotes.shared.aws

import com.amazonaws.services.sns.{AmazonSNS, AmazonSNSClientBuilder}
import scalaz.zio.IO

object SnsOps {

  private val snsClient: IO[Exception, AmazonSNS] = IO.syncException(AmazonSNSClientBuilder.defaultClient())

  // TODO topicArn should be a case class
  def putMessage(topicArn: String, messageBody: String): IO[Exception, Unit] = {
    for {
      client <- snsClient
      _ <- IO.syncException(client.publish(topicArn, messageBody))
    } yield Unit
  }

}
