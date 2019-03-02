package au.id.tmm.ausvotes.core.tallies

trait Tallier[-B, A] {
  def tally(ballot: B): A = tallyAll(List(ballot))

  def tallyAll(ballots: Iterable[B]): A
}
