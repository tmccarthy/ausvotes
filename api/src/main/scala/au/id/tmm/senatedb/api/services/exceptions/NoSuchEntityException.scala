package au.id.tmm.senatedb.api.services.exceptions

import au.id.tmm.senatedb.api.authentication.admin.AdminUser
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

sealed abstract class NoSuchEntityException
  extends Exception()

final case class NoSuchElectionException(electionId: String) extends NoSuchEntityException

final case class NoSuchStateException(stateAbbreviation: String) extends NoSuchEntityException

final case class NoSuchDivisionException(election: SenateElection, state: State, divisionName: String)
  extends NoSuchEntityException

final case class NoSuchAdminUserException(adminUser: AdminUser) extends NoSuchElementException