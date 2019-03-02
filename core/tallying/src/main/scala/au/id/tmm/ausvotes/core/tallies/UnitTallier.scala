package au.id.tmm.ausvotes.core.tallies

import cats.Monoid
import io.circe.{Encoder, Json}

case object UnitTallier extends Tallier[Any, Unit] {

  override def tallyAll(ballots: Iterable[Any]): Unit = ()

  override def tally(ballot: Any): Unit = ()

  implicit val monoidForUnit: Monoid[Unit] = new Monoid[Unit] {
    override def empty: Unit = ()

    override def combine(left: Unit, right: Unit): Unit = ()
  }

  implicit val encoder: Encoder[UnitTallier.type] = _ => Json.fromString("unit_tallier")

}
