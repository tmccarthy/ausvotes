package au.id.tmm.ausvotes.shared.io.actions

import java.time.{Instant, LocalDate, ZonedDateTime}

trait Now[F[+_, +_]] {

  def systemNanoTime: F[Nothing, Long]
  def currentTimeMillis: F[Nothing, Long]

  def nowInstant: F[Nothing, Instant]
  def nowLocalDate: F[Nothing, LocalDate]
  def nowZonedDateTime: F[Nothing, ZonedDateTime]

}

object Now {
  def systemNanoTime[F[+_, +_] : Now]: F[Nothing, Long] = implicitly[Now[F]].systemNanoTime
  def currentTimeMillis[F[+_, +_] : Now]: F[Nothing, Long] = implicitly[Now[F]].currentTimeMillis

  def nowInstant[F[+_, +_] : Now]: F[Nothing, Instant] = implicitly[Now[F]].nowInstant
  def nowLocalDate[F[+_, +_] : Now]: F[Nothing, LocalDate] = implicitly[Now[F]].nowLocalDate
  def nowZonedDateTime[F[+_, +_] : Now]: F[Nothing, ZonedDateTime] = implicitly[Now[F]].nowZonedDateTime
}
