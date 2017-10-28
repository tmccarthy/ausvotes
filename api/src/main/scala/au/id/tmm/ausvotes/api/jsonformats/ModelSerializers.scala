package au.id.tmm.ausvotes.api.jsonformats

import au.id.tmm.ausvotes.backend.persistence.daos.ElectionDao
import au.id.tmm.ausvotes.core.model.SenateElection
import org.json4s.JsonAST.{JObject, JString}
import org.json4s.{CustomSerializer, Extraction, JValue, Serializer}

object ModelSerializers {

  val electionSerializer: Serializer[SenateElection] = {

    def jObjectToElection(jValue: JValue): Option[SenateElection] = {
      jValue match {
        case jObject: JObject => jObject.values.get("id").flatMap {
          case id: String => ElectionDao.electionWithId(id)
          case _ => None
        }
        case _ => None
      }
    }

    new CustomSerializer[SenateElection](implicit formats => (Function.unlift(jObjectToElection), {
      case election: SenateElection => {
        JObject(
          "date" -> Extraction.decompose(election.date),
          "states" -> Extraction.decompose(election.states),
          "name" -> JString(election.name),
          "id" -> JString(ElectionDao.idOf(election)),
        )
      }
    }))
  }

  val ALL = Set(electionSerializer)

}
