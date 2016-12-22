package au.id.tmm.senatedb.webapp.persistence.population

trait PopulatableEntityClass {

}

object PopulatableEntityClass {

  case object Divisions extends PopulatableEntityClass
  case object PollingPlaces extends PopulatableEntityClass
  case object OtherVoteCollectionPoints extends PopulatableEntityClass

}