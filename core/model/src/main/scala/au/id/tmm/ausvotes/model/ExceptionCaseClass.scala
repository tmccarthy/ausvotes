package au.id.tmm.ausvotes.model

import scala.runtime.ScalaRunTime

// TODO remove the duplication of this
abstract class ExceptionCaseClass extends Exception with Product {
  override def getMessage: String = ScalaRunTime._toString(this)
}

object ExceptionCaseClass {
  trait WithCause { self: ExceptionCaseClass =>
    def cause: Exception

    override def getCause: Throwable = cause
  }
}
