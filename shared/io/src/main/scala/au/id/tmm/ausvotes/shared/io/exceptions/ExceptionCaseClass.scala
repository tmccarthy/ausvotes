package au.id.tmm.ausvotes.shared.io.exceptions

import scala.runtime.ScalaRunTime

abstract class ExceptionCaseClass extends Exception with Product {
  override def getMessage: String = ScalaRunTime._toString(this)
}

object ExceptionCaseClass {
  trait WithCause { self: ExceptionCaseClass =>
    def cause: Exception

    override def getCause: Throwable = cause
  }
}
