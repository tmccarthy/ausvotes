package au.id.tmm.ausvotes.shared.io

import cats.{Bimonad, CommutativeMonad, Comonad, Distributive, NonEmptyTraverse}

package object typeclasses {

  // TODO this name is wrong
  type BifunctorId[+E, A] = A

  implicit val catsBiFunctorIdInstances: Bimonad[BifunctorId[Nothing, ?]] with CommutativeMonad[BifunctorId[Nothing, ?]] with Comonad[BifunctorId[Nothing, ?]] with NonEmptyTraverse[BifunctorId[Nothing, ?]] with Distributive[BifunctorId[Nothing, ?]] = cats.catsInstancesForId

}
