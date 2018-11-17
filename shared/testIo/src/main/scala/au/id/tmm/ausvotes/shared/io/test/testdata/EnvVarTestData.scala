package au.id.tmm.ausvotes.shared.io.test.testdata

import au.id.tmm.ausvotes.shared.io.actions.EnvVars
import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO.Output

final case class EnvVarTestData(
                                 envVars: Map[String, String],
                               )

object EnvVarTestData {

  val empty = EnvVarTestData(Map.empty)

  def testIOInstance[D](envVarsField: D => EnvVarTestData): EnvVars[TestIO[D, +?, +?]] = new EnvVars[TestIO[D, +?, +?]] {
    override def envVars: TestIO[D, Nothing, Map[String, String]] =
      TestIO(data => Output(data, Right(envVarsField(data).envVars)))
  }
}
