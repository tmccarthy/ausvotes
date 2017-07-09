package au.id.tmm.senatedb.api.services.exceptions

abstract class NoSuchEntityException(entityName: String, val badIdentifier: String)
  extends Exception(s"There is no $entityName with identifier $badIdentifier")

final case class NoSuchElectionException(electionId: String)
  extends NoSuchEntityException(
    entityName = "election",
    badIdentifier = electionId
  )
