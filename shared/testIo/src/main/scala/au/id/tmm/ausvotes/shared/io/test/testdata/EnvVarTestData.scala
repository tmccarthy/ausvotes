package au.id.tmm.ausvotes.shared.io.test.testdata

import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO.Output
import au.id.tmm.bfect.extraeffects.EnvVars

final case class EnvVarTestData(
                                 envVars: Map[String, String],
                               )

object EnvVarTestData {

  val empty = EnvVarTestData(Map.empty)

  trait TestIOInstance[D] extends EnvVars[TestIO[D, +?, +?]] {
    protected def envVarsField(data: D): EnvVarTestData

    override def envVars: TestIO[D, Nothing, Map[String, String]] =
      TestIO(data => Output(data, Right(envVarsField(data).envVars)))
  }
}
