package au.id.tmm.senatedb.api.services

import javax.inject.Inject

import akka.actor.ActorRef
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Division
import au.id.tmm.senatedb.api.persistence.daos.{DivisionDao, ElectionDao}
import au.id.tmm.senatedb.api.persistence.entities.DivisionStats
import com.google.inject.name.Named

import scala.concurrent.{ExecutionContext, Future}


// TODO test
class DivisionService @Inject() (val electionDao: ElectionDao,
                                 divisionDao: DivisionDao,
                                 @Named("dbPopulationActor") val dbPopulationActor: ActorRef
                                )(implicit ec: ExecutionContext) extends DbPopulationChecks {

  def divisionStatsFor(electionId: String, stateAbbreviation: String, divisionName: String): Future[DivisionStats] =
    electionDao.withParsedElection(electionId) { election =>
      requiresElectionPopulated(election) {
        divisionDao.findStats(electionId, stateAbbreviation, divisionName)
          .map {
            case Some(divisionStats) => divisionStats
            case None => throw CannotFindStatsForDivision(election, stateAbbreviation, divisionName)
          }
      }
    }

}

case class CannotFindStatsForDivision(election: SenateElection, stateAbbreviation: String, divisionName: String) extends Exception