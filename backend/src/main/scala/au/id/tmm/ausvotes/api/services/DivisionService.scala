package au.id.tmm.ausvotes.api.services

import javax.inject.Inject

import au.id.tmm.ausvotes.api.persistence.daos.ElectionDao.withParsedElection
import au.id.tmm.ausvotes.api.persistence.daos.{DivisionDao, StatDao}
import au.id.tmm.ausvotes.api.persistence.entities.stats.Stat
import au.id.tmm.ausvotes.api.services.exceptions.{NoSuchDivisionException, NoSuchStateException}
import au.id.tmm.ausvotes.core.model.parsing.Division
import au.id.tmm.utilities.concurrent.FutureUtils.TryFutureOps
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.option.OptionUtils.ImprovedOption

import scala.concurrent.{ExecutionContext, Future}

class DivisionService @Inject() (divisionDao: DivisionDao,
                                 statDao: StatDao,
                                )(implicit ec: ExecutionContext) {

  def divisionWith(electionId: String, stateAbbreviation: String, divisionName: String): Future[Division] =
    withParsedElection(electionId) { election =>
      for {
        state <- State.fromAbbreviation(stateAbbreviation)
          .failIfAbsent(NoSuchStateException(stateAbbreviation))
          .toFuture
        maybeDivision <- divisionDao.find(election, state, divisionName)
      } yield maybeDivision.failIfAbsent(NoSuchDivisionException(election, state, divisionName)).get
    }

  def statsFor(electionId: String, stateAbbreviation: String, divisionName: String): Future[Set[Stat[Division]]] =
    withParsedElection(electionId) { election =>
      Future.fromTry {
        State.fromAbbreviation(stateAbbreviation)
          .failIfAbsent(NoSuchStateException(stateAbbreviation))
      }.flatMap { state =>
        divisionDao.find(election, state, divisionName)
          .flatMap { maybeDivision =>
            val division = maybeDivision
              .failIfAbsent(NoSuchDivisionException(election, state, divisionName))
              .get

            statDao.statsFor(division)
          }
      }
    }

}
