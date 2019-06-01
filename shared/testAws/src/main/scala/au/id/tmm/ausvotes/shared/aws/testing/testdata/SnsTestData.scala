package au.id.tmm.ausvotes.shared.aws.testing.testdata

import au.id.tmm.ausvotes.shared.aws.actions.SnsActions.PutsSnsMessages
import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.bfect.testing.BState

final case class SnsTestData(
                              snsMessagesPerTopic: Map[String, List[String]],
                            ) {
  def writeMessage(topic: String, message: String): SnsTestData = {
    val mapWithDefaults = this.snsMessagesPerTopic.withDefaultValue(Nil)

    copy(
      snsMessagesPerTopic = mapWithDefaults.updated(topic, mapWithDefaults(topic) :+ message)
    )
  }
}

object SnsTestData {

  val empty = SnsTestData(Map.empty)

  trait TestIOInstance[D] extends PutsSnsMessages[BState[D, +?, +?]] {
    protected def snsTestDataField(data: D): SnsTestData
    protected def setSnsTestData(oldData: D, newTestData: SnsTestData): D

    override def putMessage(topicArn: String, messageBody: String): BState[D, Exception, Unit] =
      TestIO { oldTestData =>
        val oldSnsTestData = snsTestDataField(oldTestData)
        val newSnsTestData = oldSnsTestData.writeMessage(topicArn, messageBody)
        val newTestData = setSnsTestData(oldTestData, newSnsTestData)
        (newTestData, Right(()))
      }
  }

}
