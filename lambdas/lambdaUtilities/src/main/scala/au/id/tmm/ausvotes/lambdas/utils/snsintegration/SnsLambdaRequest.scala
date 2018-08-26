package au.id.tmm.ausvotes.lambdas.utils.snsintegration

import java.time.Instant
import java.util.UUID

import argonaut.{DecodeJson, DecodeResult}
import au.id.tmm.ausvotes.lambdas.utils.DateTimeCodecs._
import au.id.tmm.ausvotes.lambdas.utils.snsintegration.SnsLambdaRequest.SnsBody
import au.id.tmm.ausvotes.lambdas.utils.snsintegration.SnsLambdaRequest.SnsBody.MessageAttributes

final case class SnsLambdaRequest(
                                   eventVersion: String,
                                   eventSubscriptionArn: String,
                                   eventSource: String,

                                   snsBody: SnsBody,
                                 )

object SnsLambdaRequest {
  final case class SnsBody(
                            signatureVersion: String,
                            timestamp: Instant,
                            signature: String,
                            signingCertUrl: String,
                            messageId: UUID,
                            message: String,
                            messageAttributes: MessageAttributes,
                            eventType: String,
                            unsubscribeUrl: String,
                            topicArn: String,
                            subject: String,
                          )

  object SnsBody {
    final case class MessageAttributes()

    object MessageAttributes {
      implicit val decode: DecodeJson[MessageAttributes] = c => DecodeResult.ok(MessageAttributes())
    }

    implicit val decode: DecodeJson[SnsBody] = c => {
      for {
        signatureVersion <- c.downField("SignatureVersion").as[String]
        timestamp <- c.downField("Timestamp").as[Instant]
        signature <- c.downField("Signature").as[String]
        signingCertUrl <- c.downField("SigningCertUrl").as[String]
        messageId <- c.downField("MessageId").as[UUID]
        message <- c.downField("Message").as[String]
        messageAttributes <- c.downField("MessageAttributes").as[MessageAttributes]
        eventType <- c.downField("Type").as[String]
        unsubscribeUrl <- c.downField("UnsubscribeUrl").as[String]
        topicArn <- c.downField("TopicArn").as[String]
        subject <- c.downField("Subject").as[String]
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

  implicit val decode: DecodeJson[SnsLambdaRequest] = rootCursor => {
    val c = rootCursor.downField("Records").downN(0)

    for {
      eventVersion <- c.downField("EventVersion").as[String]
      eventSubscriptionArn <- c.downField("EventSubscriptionArn").as[String]
      eventSource <- c.downField("EventSource").as[String]

      snsBody <- c.downField("Sns").as[SnsBody]

    } yield SnsLambdaRequest(
      eventVersion,
      eventSubscriptionArn,
      eventSource,
      snsBody,
    )
  }
}
