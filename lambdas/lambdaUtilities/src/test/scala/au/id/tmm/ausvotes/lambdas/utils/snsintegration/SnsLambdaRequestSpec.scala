package au.id.tmm.ausvotes.lambdas.utils.snsintegration

import java.time.Instant
import java.util.UUID

import argonaut.{DecodeJson, Parse}
import au.id.tmm.ausvotes.lambdas.utils.snsintegration.SnsLambdaRequest.SnsBody.MessageAttributes
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class SnsLambdaRequestSpec extends ImprovedFlatSpec {

  "a request" can "be decoded" in {
    final case class Message(message: String)

    implicit val decodeMessage: DecodeJson[Message] = c => c.downField("message").as[String].map(Message)

    val requestJson =
      """
        |{
        |  "Records": [
        |    {
        |      "EventVersion": "1.0",
        |      "EventSubscriptionArn": "eventsubscriptionarn",
        |      "EventSource": "aws:sns",
        |      "Sns": {
        |        "SignatureVersion": "1",
        |        "Timestamp": "1970-01-01T00:00:00.000Z",
        |        "Signature": "EXAMPLE",
        |        "SigningCertUrl": "EXAMPLE",
        |        "MessageId": "95df01b4-ee98-5cb9-9903-4c221d41eb5e",
        |        "Message": "{ \"message\": \"test message\" }",
        |        "MessageAttributes": {
        |          "Test": {
        |            "Type": "String",
        |            "Value": "TestString"
        |          },
        |          "TestBinary": {
        |            "Type": "Binary",
        |            "Value": "TestBinary"
        |          }
        |        },
        |        "Type": "Notification",
        |        "UnsubscribeUrl": "EXAMPLE",
        |        "TopicArn": "topicarn",
        |        "Subject": "TestInvoke"
        |      }
        |    }
        |  ]
        |}
        |""".stripMargin

    val expectedRequest = SnsLambdaRequest[Message](
      eventVersion = "1.0",
      eventSubscriptionArn = "eventsubscriptionarn",
      eventSource = "aws:sns",
      snsBody = SnsLambdaRequest.SnsBody[Message](
        signatureVersion = "1",
        timestamp = Instant.ofEpochMilli(0),
        signature = "EXAMPLE",
        signingCertUrl = "EXAMPLE",
        messageId = UUID.fromString("95df01b4-ee98-5cb9-9903-4c221d41eb5e"),
        message = Message("test message"),
        messageAttributes = MessageAttributes(),
        eventType = "Notification",
        unsubscribeUrl = "EXAMPLE",
        topicArn = "topicarn",
        subject = "TestInvoke",
      ),
    )

    assert(Parse.decodeEither[SnsLambdaRequest[Message]](requestJson) === Right(expectedRequest))
  }

}
