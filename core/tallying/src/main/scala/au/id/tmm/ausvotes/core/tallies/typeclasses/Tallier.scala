package au.id.tmm.ausvotes.core.tallies.typeclasses

trait Tallier[T, B, A] {
  def tally(t: T)(ballot: B): A = tallyAll(t)(List(ballot))

  def tallyAll(t: T)(ballots: Iterable[B]): A
}

object Tallier {
  def apply[T, B, A](implicit tallier: Tallier[T, B, A]): Tallier[T, B, A] = implicitly[Tallier[T, B, A]]
}
