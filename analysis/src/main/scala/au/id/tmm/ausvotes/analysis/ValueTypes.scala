package au.id.tmm.ausvotes.analysis

import cats.instances.double._
import cats.instances.int._
import cats.kernel.CommutativeGroup

object ValueTypes {

  final case class VotedFormally(asInt: Int) extends AnyVal

  object VotedFormally {
    implicit val instances: CommutativeGroup[VotedFormally] = commutativeGroupFor(VotedFormally.apply, _.asInt)
  }

  object UsedHtv {

    final case class Nominal(asInt: Int) extends AnyVal {
      def / (votedFormally: VotedFormally): Percentage =
        Percentage((this.asInt.toDouble / votedFormally.asInt.toDouble) * 100)
    }

    object Nominal {
      implicit val instances: CommutativeGroup[Nominal] = commutativeGroupFor(Nominal.apply, _.asInt)
    }

    final case class Percentage(asDouble: Double) extends AnyVal

    object Percentage {
      implicit val instances: CommutativeGroup[Percentage] = commutativeGroupFor(Percentage.apply, _.asDouble)
    }

  }

  private def commutativeGroupFor[A, B : CommutativeGroup](apply: B => A, unapply: A => B): CommutativeGroup[A] =
    new CommutativeGroup[A] {
      private val underlying: CommutativeGroup[B] = CommutativeGroup[B]

      override def inverse(a: A): A = apply(underlying.inverse(unapply(a)))
      override def empty: A = apply(underlying.empty)
      override def combine(x: A, y: A): A = apply(underlying.combine(unapply(x), unapply(y)))
      override def remove(x: A, y: A): A = apply(underlying.remove(unapply(x), unapply(y)))
    }

}
