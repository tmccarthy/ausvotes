package au.id.tmm.ausvotes.shared.io.test.datatraits

import java.time.{Duration, Instant}

trait CurrentTime[D] {
  def initialTime: Instant
  def stepEachInvocation: Duration
  protected def copyWithInitialTime(initialTime: Instant): D

  def increment: (Instant, D) =
    (initialTime, this.copyWithInitialTime(initialTime.plus(stepEachInvocation)))
}

