package au.id.tmm.ausvotes.shared.io.test.testdata

import au.id.tmm.bfect.effects.extra.EnvVars
import au.id.tmm.bfect.testing.BState

final case class EnvVarTestData(
                                 envVars: Map[String, String],
                               )

object EnvVarTestData {

  val empty = EnvVarTestData(Map.empty)

  trait TestIOInstance[D] extends EnvVars.WithBMonad[BState[D, +?, +?]] with BState.BMEInstance[D] {
    protected def envVarsField(data: D): EnvVarTestData

    override def envVars: BState[D, Nothing, Map[String, String]] =
      BState(data => (data, Right(envVarsField(data).envVars)))
  }
}
