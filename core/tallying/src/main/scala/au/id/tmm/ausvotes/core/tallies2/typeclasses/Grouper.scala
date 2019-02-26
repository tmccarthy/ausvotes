package au.id.tmm.ausvotes.core.tallies2.typeclasses

trait Grouper[F, G, B] {
  def groupsOf(grouper: F)(ballot: B): Set[G]
}

object Grouper {
  def apply[F, G, B](implicit grouper: Grouper[F, G, B]): Grouper[F, G, B] = implicitly[Grouper[F, G, B]]
}
