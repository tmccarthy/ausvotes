package au.id.tmm.ausvotes.shared.aws.actions

import au.id.tmm.ausvotes.shared.aws.data.LambdaFunctionName

object LambdaActions {

  abstract class InvokesLambda[F[+_, +_]] {
    def invokeFunction(name: LambdaFunctionName, payload: Option[String]): F[Exception, String]
  }

  object InvokesLambda {
    def invokeFunction[F[+_, +_] : InvokesLambda](name: LambdaFunctionName, payload: Option[String]): F[Exception, String] =
      implicitly[InvokesLambda[F]].invokeFunction(name, payload)
  }

}
