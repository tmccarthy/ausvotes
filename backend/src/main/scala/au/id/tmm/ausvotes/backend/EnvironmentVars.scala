package au.id.tmm.ausvotes.backend

object EnvironmentVars {

  def dbUrl: String = sys.env.getOrElse("DB_DEFAULT_URL", "jdbc:postgresql://localhost/ausvotes_backend")
  def dbUser: String = sys.env.getOrElse("DB_DEFAULT_USER", "ausvotes_backend")
  def dbPasswordFile: Option[String] = sys.env.get("DB_DEFAULT_PASSWORD_FILE")

}
