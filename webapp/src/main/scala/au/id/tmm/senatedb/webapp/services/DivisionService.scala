package au.id.tmm.senatedb.webapp.services

import javax.inject.Inject

import akka.actor.ActorRef
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Division
import au.id.tmm.senatedb.webapp.persistence.daos.{DivisionDao, ElectionDao}
import au.id.tmm.senatedb.webapp.persistence.entities.DivisionStats
import com.google.inject.name.Named

import scala.concurrent.{ExecutionContext, Future}


// TODO test
class DivisionService @Inject() (val electionDao: ElectionDao,
                                 divisionDao: DivisionDao,
                                 @Named("dbPopulationActor") val dbPopulationActor: ActorRef
                                )(implicit ec: ExecutionContext) extends DbPopulationChecks {

  def divisionWithStatsFor(electionId: String, stateAbbreviation: String, divisionName: String): Future[(Division, DivisionStats)] =
    electionDao.withParsedElection(electionId) { election =>
      requiresElectionPopulated(election) {
        divisionDao.findWithStats(electionId, stateAbbreviation, divisionName)
          .map {
            case Some(divisionAndStats) => divisionAndStats
            case None => throw CannotFindStatsForDivision(election, stateAbbreviation, divisionName)
          }
      }
    }

}

case class CannotFindStatsForDivision(election: SenateElection, stateAbbreviation: String, divisionName: String) extends Exception
