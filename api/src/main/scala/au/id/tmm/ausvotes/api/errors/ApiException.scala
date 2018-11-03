package au.id.tmm.ausvotes.api.errors

import scala.runtime.ScalaRunTime

abstract class ApiException extends Exception with Product {
  override def getMessage: String = ScalaRunTime._toString(this)
}

object ApiException {
  trait WithCause { self: ApiException =>
    def cause: Exception

    override def getCause: Throwable = cause
  }
}
