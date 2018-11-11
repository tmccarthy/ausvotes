package au.id.tmm.ausvotes.api.errors

import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass

abstract class ConfigException extends ExceptionCaseClass

object ConfigException {
  final case class InvalidConfig(envVar: String, invalidValue: String) extends ConfigException
  final case class EnvVarMissing(envVar: String) extends ConfigException
}
