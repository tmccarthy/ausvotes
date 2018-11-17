package au.id.tmm.ausvotes.shared.aws.testing.testdata

import au.id.tmm.ausvotes.shared.aws.actions.LambdaActions.InvokesLambda
import au.id.tmm.ausvotes.shared.aws.data.LambdaFunctionName
import au.id.tmm.ausvotes.shared.aws.testing.testdata.LambdaTestData.LambdaInvocation
import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO.Output

final case class LambdaTestData(
                                 handler: LambdaTestData.Responder,
                                 invocations: List[LambdaInvocation] = List.empty,
                               ) {

  def invoke(function: LambdaFunctionName, payload: Option[String]): (LambdaTestData, Either[Exception, String]) = {
    this.copy(invocations = this.invocations :+ LambdaInvocation(function, payload)) -> handler(LambdaInvocation(function, payload))
  }

}

object LambdaTestData {

  final case class LambdaInvocation(functionName: LambdaFunctionName, payload: Option[String])
  type Responder = PartialFunction[LambdaInvocation, Either[Exception, String]]

  val alwaysFailHandler: Responder =
    PartialFunction.empty

  val default = LambdaTestData(handler = alwaysFailHandler)

  def testIOInstance[D](
                         lambdaTestDataField: D => LambdaTestData,
                         setLambdaTestData: (D, LambdaTestData) => D,
                       ): InvokesLambda[TestIO[D, +?, +?]] = new InvokesLambda[TestIO[D, +?, +?]] {
    override def invokeFunction(name: LambdaFunctionName, payload: Option[String]): TestIO[D, Exception, String] =
      TestIO { oldTestData =>
        val oldLambdaTestData = lambdaTestDataField(oldTestData)
        val (newLambdaTestData, lambdaResult) = oldLambdaTestData.invoke(name, payload)
        val newTestData = setLambdaTestData(oldTestData, newLambdaTestData)
        Output(newTestData, lambdaResult)
      }
  }

}
