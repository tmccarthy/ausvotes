package au.id.tmm.ausvotes.core.tallies

// TODO change order of type params
trait Grouper[G, B] {
  def groupsOf(ballot: B): Set[G]
}
