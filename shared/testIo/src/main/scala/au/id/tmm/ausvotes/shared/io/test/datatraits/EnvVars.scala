package au.id.tmm.ausvotes.shared.io.test.datatraits

trait EnvVars[D] {
  def envVars: Map[String, String]
}
