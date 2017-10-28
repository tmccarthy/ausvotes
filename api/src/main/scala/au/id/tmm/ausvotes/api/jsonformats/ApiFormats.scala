package au.id.tmm.ausvotes.api.jsonformats

import org.json4s.{DefaultFormats, Serializer}

object ApiFormats extends DefaultFormats {

  override val customSerializers: List[Serializer[_]] = List() ++
    TemporalSerializers.ALL ++
    ModelSerializers.ALL ++
    ErrorResponseSerializers.ALL

}
