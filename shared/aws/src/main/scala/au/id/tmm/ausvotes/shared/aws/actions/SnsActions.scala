package au.id.tmm.ausvotes.shared.aws.actions

object SnsActions {

  trait PutsSnsMessages[F[+_, +_]] {
    def putMessage(topicArn: String, messageBody: String): F[Exception, Unit]
  }

  object PutsSnsMessages {
    def putSnsMessage[F[+_, +_] : PutsSnsMessages](topicArn: String, messageBody: String): F[Exception, Unit] =
      implicitly[PutsSnsMessages[F]].putMessage(topicArn, messageBody)
  }

}
