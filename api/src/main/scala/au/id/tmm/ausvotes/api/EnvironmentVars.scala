package au.id.tmm.ausvotes.api

object EnvironmentVars {
  def environmentKey: String = sys.env.getOrElse("ENVIRONMENT_KEY", "development")

  def contextPath: String = sys.env.getOrElse("CONTEXT_PATH", "/api")
  def hostName: String = sys.env.getOrElse("HOST_NAME", "localhost")
  def port: Int = sys.env.getOrElse("PORT", "80").toInt
  def forceHttps: Boolean = sys.env.getOrElse("FORCE_HTTPS", "false").toBoolean
}
