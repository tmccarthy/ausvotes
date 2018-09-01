package au.id.tmm.ausvotes.lambdas.recountenqueue

import com.amazonaws.services.sns.{AmazonSNS, AmazonSNSClientBuilder}
import scalaz.zio.IO

// TODO make this shared
object SnsOps {

  private val snsClient: IO[Exception, AmazonSNS] = IO.syncException(AmazonSNSClientBuilder.defaultClient())

  def putMessage(topicArn: String, messageBody: String): IO[Exception, Unit] = {
    for {
      client <- snsClient
      _ <- IO.syncException(client.publish(topicArn, messageBody))
    } yield Unit
  }

}
