package au.id.tmm.senatedb.api.persistence.population

trait PopulatableEntityClass {

}

object PopulatableEntityClass {

  case object Divisions extends PopulatableEntityClass
  case object PollingPlaces extends PopulatableEntityClass
  case object OtherVoteCollectionPoints extends PopulatableEntityClass

}