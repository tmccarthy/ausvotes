package au.id.tmm.senatedb.api.services

import javax.inject.Inject

import akka.actor.ActorRef
import au.id.tmm.senatedb.api.persistence.daos.DivisionDao
import com.google.inject.name.Named

import scala.concurrent.ExecutionContext


// TODO test
class DivisionService @Inject() (divisionDao: DivisionDao,
                                 @Named("dbPopulationActor") val dbPopulationActor: ActorRef
                                )(implicit ec: ExecutionContext) extends DbPopulationChecks {

}
