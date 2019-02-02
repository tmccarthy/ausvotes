package au.id.tmm.ausvotes.analysis

object Joins {

  def innerJoin[L, R, A](left: Iterable[L],  right: Iterable[R])
                        (leftToJoin: L => A, rightToJoin: R => A): List[(A, L, R)] = {
    val rightGrouped: Map[A, List[R]] = right.toList.groupBy(rightToJoin)

    left.flatMap { leftElement =>
      val leftJoinKey = leftToJoin(leftElement)
      val rightElements = rightGrouped.getOrElse(leftJoinKey, Nil)

      rightElements.map { rightElement =>
        (leftJoinKey, leftElement, rightElement)
      }
    }.toList
  }

  def innerJoinUsing[L, R, A](left: Iterable[(A, L)], right: Iterable[(A, R)]): List[(A, L, R)] = {
    innerJoin(left, right)(x => x._1, x => x._1).map { case (a, (_, l), (_, r)) =>
      (a, l, r)
    }
  }

}
