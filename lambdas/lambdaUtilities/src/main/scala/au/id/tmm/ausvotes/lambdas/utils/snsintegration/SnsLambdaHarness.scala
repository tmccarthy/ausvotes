package au.id.tmm.ausvotes.lambdas.utils.snsintegration

import argonaut.DecodeJson
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness

abstract class SnsLambdaHarness[T_MESSAGE : DecodeJson, T_ERROR] extends LambdaHarness[SnsLambdaRequest[T_MESSAGE], Unit, T_ERROR] {

  override protected final def errorResponseTransformer: LambdaHarness.ErrorResponseTransformer[Unit, T_ERROR] =
    error => Unit

  override protected final def transformHarnessError(harnessInputError: LambdaHarness.HarnessInputError): Unit = Unit

}
