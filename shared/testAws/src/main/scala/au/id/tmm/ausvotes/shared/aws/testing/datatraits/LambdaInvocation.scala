package au.id.tmm.ausvotes.shared.aws.testing.datatraits

import au.id.tmm.ausvotes.shared.aws.LambdaOps.LambdaInvocationException
import au.id.tmm.ausvotes.shared.aws.data.LambdaFunctionName
import au.id.tmm.http_constants.HttpResponseCode

trait LambdaInvocation[D] {

  def lambdaCallHandler: (LambdaFunctionName, Option[String]) => Either[Exception, String]

}

object LambdaInvocation {
  val alwaysFailHandler: (LambdaFunctionName, Option[String]) => Either[Exception, String] = {
    case (_, _) => Left(LambdaInvocationException(HttpResponseCode.InternalServerError, handled = false, "No such function"))
  }
}
