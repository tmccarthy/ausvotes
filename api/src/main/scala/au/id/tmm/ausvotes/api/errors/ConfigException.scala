package au.id.tmm.ausvotes.api.errors

abstract class ConfigException extends ApiException

object ConfigException {
  final case class InvalidConfig(envVar: String, invalidValue: String) extends ConfigException
  final case class EnvVarMissing(envVar: String) extends ConfigException
}
