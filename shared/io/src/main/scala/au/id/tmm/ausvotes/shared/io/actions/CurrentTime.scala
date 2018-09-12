package au.id.tmm.ausvotes.shared.io.actions

import java.time.{Instant, LocalDate, ZonedDateTime}

abstract class CurrentTime[F[+_, +_]] {

  def systemNanoTime: F[Nothing, Long]
  def currentTimeMillis: F[Nothing, Long]

  def nowInstant: F[Nothing, Instant]
  def nowLocalDate: F[Nothing, LocalDate]
  def nowZonedDateTime: F[Nothing, ZonedDateTime]

}
