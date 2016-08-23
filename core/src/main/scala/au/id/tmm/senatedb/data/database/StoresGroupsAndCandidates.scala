package au.id.tmm.senatedb.data.database

import au.id.tmm.senatedb.model.SenateElection

import scala.concurrent.Future

private[database] trait StoresGroupsAndCandidates { this: Persistence =>
  import dal.driver.api._

  def storeGroups(groups: Set[GroupsRow]): Future[Unit] =
    execute(dal.insertGroups(groups)).map(_ => Unit)

  def storeCandidates(candidates: Set[CandidatesRow]): Future[Unit] =
    execute(dal.insertCandidates(candidates)).map(_ => Unit)

  def hasGroupsFor(election: SenateElection): Future[Boolean] = {
    val query = dal.groups
      .filter(_.election === election.aecID)
      .exists

    execute(query.result)
  }

  def hasCandidatesFor(election: SenateElection): Future[Boolean] = {
    val query = dal.candidates
      .filter(_.election === election.aecID)
      .exists

    execute(query.result)
  }

  def deleteGroupsFor(election: SenateElection): Future[Unit] = {
    val deleteStatement = dal.groups
      .filter(_.election === election.aecID)
      .delete

    execute(deleteStatement).map(_ => Unit)
  }

  def deleteCandidatesFor(election: SenateElection): Future[Unit] = {
    val deleteStatement = dal.candidates
      .filter(_.election === election.aecID)
      .delete

    execute(deleteStatement).map(_ => Unit)
  }

}
