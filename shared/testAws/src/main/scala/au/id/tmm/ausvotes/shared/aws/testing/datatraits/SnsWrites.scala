package au.id.tmm.ausvotes.shared.aws.testing.datatraits

trait SnsWrites[D] {
  def snsMessagesPerTopic: Map[String, List[String]]

  protected def copyWithSnsMessages(snsMessagesPerTopic: Map[String, List[String]]): D

  def writeMessage(topic: String, message: String): D = {
    val mapWithDefaults = this.snsMessagesPerTopic.withDefaultValue(Nil)

    copyWithSnsMessages(
      mapWithDefaults.updated(topic, mapWithDefaults(topic) :+ message)
    )
  }
}
