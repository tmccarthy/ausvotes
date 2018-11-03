package au.id.tmm.ausvotes.shared.aws.testing.datatraits

import au.id.tmm.ausvotes.shared.aws.data.LambdaFunctionName

trait LambdaInteraction[D] {

  def lambdaInvocations: List[LambdaInteraction.Invocation]

  def lambdaCallHandler: LambdaInteraction.Responder

  protected def copyWithLambdaInvocations(invocations: List[LambdaInteraction.Invocation]): D

  def invoke(function: LambdaFunctionName, body: Option[String]): (D, Either[Exception, String]) = {
    (copyWithLambdaInvocations(this.lambdaInvocations :+ (function, body)), lambdaCallHandler(function, body))
  }

}

object LambdaInteraction {
  type Invocation = (LambdaFunctionName, Option[String])
  type Responder = PartialFunction[Invocation, Either[Exception, String]]

  val alwaysFailHandler: Responder =
    PartialFunction.empty
}
