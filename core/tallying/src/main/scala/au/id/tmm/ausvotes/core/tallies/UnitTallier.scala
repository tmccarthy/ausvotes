package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.tallies.typeclasses.Tallier
import cats.Monoid
import io.circe.{Encoder, Json}

case object UnitTallier {

  implicit def UnitTallierIsATallier[B]: Tallier[UnitTallier.type, B, Unit] = new Tallier[UnitTallier.type, B, Unit] {
    override def tallyAll(t: UnitTallier.type)(ballots: Iterable[B]): Unit = ()
  }

  implicit val monoidForUnit: Monoid[Unit] = new Monoid[Unit] {
    override def empty: Unit = ()

    override def combine(left: Unit, right: Unit): Unit = ()
  }

  implicit val encoder: Encoder[UnitTallier.type] = _ => Json.fromString("unit_tallier")

}
