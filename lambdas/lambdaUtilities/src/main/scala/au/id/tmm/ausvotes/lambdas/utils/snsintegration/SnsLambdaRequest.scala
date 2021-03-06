package au.id.tmm.ausvotes.lambdas.utils.snsintegration

import java.time.Instant
import java.util.UUID

import au.id.tmm.ausvotes.lambdas.utils.snsintegration.SnsLambdaRequest.SnsBody
import au.id.tmm.ausvotes.lambdas.utils.snsintegration.SnsLambdaRequest.SnsBody.MessageAttributes
import cats.syntax.show.toShow
import io.circe.parser._
import io.circe.{Decoder, DecodingFailure}

final case class SnsLambdaRequest[A](
                                      eventVersion: String,
                                      eventSubscriptionArn: String,
                                      eventSource: String,

                                      snsBody: SnsBody[A],
                                    )

object SnsLambdaRequest {
  final case class SnsBody[A](
                               signatureVersion: String,
                               timestamp: Instant,
                               signature: String,
                               signingCertUrl: String,
                               messageId: UUID,
                               message: A,
                               messageAttributes: MessageAttributes,
                               eventType: String,
                               unsubscribeUrl: String,
                               topicArn: String,
                               subject: Option[String],
                             )

  object SnsBody {
    final case class MessageAttributes()

    object MessageAttributes {
      implicit val decode: Decoder[MessageAttributes] = c => Right(MessageAttributes())
    }

    implicit def decode[A : Decoder]: Decoder[SnsBody[A]] = c => {
      for {
        signatureVersion <- c.downField("SignatureVersion").as[String]
        timestamp <- c.downField("Timestamp").as[Instant]
        signature <- c.downField("Signature").as[String]
        signingCertUrl <- c.downField("SigningCertUrl").as[String]
        messageId <- c.downField("MessageId").as[UUID]
        messageString <- c.downField("Message").as[String]
        messageJson <- parse(messageString).left.map(message => DecodingFailure(message.show, c.history))
        message <- messageJson.as[A]
        messageAttributes <- c.downField("MessageAttributes").as[MessageAttributes]
        eventType <- c.downField("Type").as[String]
        unsubscribeUrl <- c.downField("UnsubscribeUrl").as[String]
        topicArn <- c.downField("TopicArn").as[String]
        subject <- c.downField("Subject").as[Option[String]]
      } yield SnsBody(
        signatureVersion,
        timestamp,
        signature,
        signingCertUrl,
        messageId,
        message,
        messageAttributes,
        eventType,
        unsubscribeUrl,
        topicArn,
        subject,
      )
    }
  }

  implicit def decode[A : Decoder]: Decoder[SnsLambdaRequest[A]] = rootCursor => {
    val c = rootCursor.downField("Records").downN(0)

    for {
      eventVersion <- c.downField("EventVersion").as[String]
      eventSubscriptionArn <- c.downField("EventSubscriptionArn").as[String]
      eventSource <- c.downField("EventSource").as[String]

      snsBody <- c.downField("Sns").as[SnsBody[A]]

    } yield SnsLambdaRequest(
      eventVersion,
      eventSubscriptionArn,
      eventSource,
      snsBody,
    )
  }
}
