package au.id.tmm.ausvotes.api.jsonformats

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.time.temporal.TemporalAccessor

import org.json4s.{CustomSerializer, JString, Serializer}

object TemporalSerializers {

  private def temporalSerializer[A <: TemporalAccessor: Manifest](formatter: DateTimeFormatter, fromTemporal: TemporalAccessor => A): Serializer[A] = {
    def canParse(string: String): Boolean = {
      try {
        formatter.parse(string)
        true
      } catch {
        case _: DateTimeParseException => false
      }
    }

    val Class = implicitly[Manifest[A]].runtimeClass

    new CustomSerializer[A](formats => ({
      case JString(string) if canParse(string) => fromTemporal(formatter.parse(string))
    }, {
      case x: Any if Class.isInstance(x) => JString(formatter.format(x.asInstanceOf[A]))
    }))
  }

  val localDateSerialiser = temporalSerializer[LocalDate](DateTimeFormatter.ISO_LOCAL_DATE, LocalDate.from)

  val ALL = Set(localDateSerialiser)
}
