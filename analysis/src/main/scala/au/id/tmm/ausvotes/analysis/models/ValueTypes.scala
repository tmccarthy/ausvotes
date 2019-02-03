package au.id.tmm.ausvotes.analysis.models

import cats.instances.double._
import cats.instances.int._
import cats.kernel.CommutativeGroup

object ValueTypes {

  final case class VotedFormally(asInt: Int) extends AnyVal

  object VotedFormally extends IntValueTypeCompanion[VotedFormally](new VotedFormally(_), _.asInt)

  object UsedHtv {

    final case class Nominal(asInt: Int) extends AnyVal {
      def / (votedFormally: VotedFormally): Percentage =
        Percentage(((this.asInt.toDouble / votedFormally.asInt.toDouble) * 10000).round.toDouble / 100.0)
    }

    object Nominal extends IntValueTypeCompanion[Nominal](new Nominal(_), _.asInt)

    final case class Percentage(asDouble: Double) extends AnyVal

    object Percentage extends ValueTypeCompanion[Percentage, Double](new Percentage(_), _.asDouble)

  }

  sealed abstract class ValueTypeCompanion[A, T_UNDERLYING : Ordering : CommutativeGroup](
                                                                                           private val makeValueType: T_UNDERLYING => A,
                                                                                           private val extractUnderlying: A => T_UNDERLYING,
                                                                                         ) {

    implicit val monoidInstance: CommutativeGroup[A] = new CommutativeGroup[A] {
      private val underlying: CommutativeGroup[T_UNDERLYING] = CommutativeGroup[T_UNDERLYING]

      override def inverse(a: A): A = makeValueType(underlying.inverse(extractUnderlying(a)))
      override def empty: A = makeValueType(underlying.empty)
      override def combine(x: A, y: A): A = makeValueType(underlying.combine(extractUnderlying(x), extractUnderlying(y)))
      override def remove(x: A, y: A): A = makeValueType(underlying.remove(extractUnderlying(x), extractUnderlying(y)))
    }

    implicit val ordering: Ordering[A] = Ordering.by(extractUnderlying)
  }

  sealed abstract class IntValueTypeCompanion[A](
                                                  private val makeValueType: Int => A,
                                                  private val extractUnderlying: A => Int,
                                                ) extends ValueTypeCompanion[A, Int](makeValueType, extractUnderlying) {
    def apply(asDouble: Double): A = makeValueType(asDouble.toInt)
  }

}
