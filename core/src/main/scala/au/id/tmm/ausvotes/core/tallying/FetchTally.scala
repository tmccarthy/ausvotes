package au.id.tmm.ausvotes.core.tallying

import au.id.tmm.ausvotes.core.tallies._
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.Concurrent
import io.circe.{Decoder, Encoder}

trait FetchTally[F[+_, +_]] {

  def fetchTally0(election: SenateElection, tallier: Tallier0): F[FetchTally.Error, Tally0]

  def fetchTally1[T_GROUP_1 : Encoder : Decoder](election: SenateElection, tallier: Tallier1[T_GROUP_1]): F[FetchTally.Error, Tally1[T_GROUP_1]]

  def fetchTally2[T_GROUP_1 : Encoder : Decoder, T_GROUP_2 : Encoder : Decoder](election: SenateElection, tallier: Tallier2[T_GROUP_1, T_GROUP_2]): F[FetchTally.Error, Tally2[T_GROUP_1, T_GROUP_2]]

  def fetchTally3[T_GROUP_1 : Encoder : Decoder, T_GROUP_2 : Encoder : Decoder, T_GROUP_3 : Encoder : Decoder](election: SenateElection, tallier: Tallier3[T_GROUP_1, T_GROUP_2, T_GROUP_3]): F[FetchTally.Error, Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]]

}

object FetchTally {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  def fetchTally0[F[+_, +_] : FetchTally](election: SenateElection, tallier: Tallier0): F[FetchTally.Error, Tally0] =
    implicitly[FetchTally[F]].fetchTally0(election, tallier)

  def fetchTally1[F[+_, +_] : FetchTally, T_GROUP_1 : Encoder : Decoder](election: SenateElection, tallier: Tallier1[T_GROUP_1]): F[FetchTally.Error, Tally1[T_GROUP_1]] =
    implicitly[FetchTally[F]].fetchTally1(election, tallier)

  def fetchTally2[F[+_, +_] : FetchTally, T_GROUP_1 : Encoder : Decoder, T_GROUP_2 : Encoder : Decoder](election: SenateElection, tallier: Tallier2[T_GROUP_1, T_GROUP_2]): F[FetchTally.Error, Tally2[T_GROUP_1, T_GROUP_2]] =
    implicitly[FetchTally[F]].fetchTally2(election, tallier)

  def fetchTally3[F[+_, +_] : FetchTally, T_GROUP_1 : Encoder : Decoder, T_GROUP_2 : Encoder : Decoder, T_GROUP_3 : Encoder : Decoder](election: SenateElection, tallier: Tallier3[T_GROUP_1, T_GROUP_2, T_GROUP_3]): F[FetchTally.Error, Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]] =
    implicitly[FetchTally[F]].fetchTally3(election, tallier)

  def fetch10Tallies[F[+_, +_] : FetchTally : Concurrent, T1 <: Tally, T2 <: Tally, T3 <: Tally, T4 <: Tally, T5 <: Tally, T6 <: Tally, T7 <: Tally, T8 <: Tally, T9 <: Tally, T10 <: Tally]
  (
    fetchTally1: F[FetchTally.Error, T1],
    fetchTally2: F[FetchTally.Error, T2],
    fetchTally3: F[FetchTally.Error, T3],
    fetchTally4: F[FetchTally.Error, T4],
    fetchTally5: F[FetchTally.Error, T5],
    fetchTally6: F[FetchTally.Error, T6],
    fetchTally7: F[FetchTally.Error, T7],
    fetchTally8: F[FetchTally.Error, T8],
    fetchTally9: F[FetchTally.Error, T9],
    fetchTally10: F[FetchTally.Error, T10],
  ): F[FetchTally.Error, (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)] = {
    val concurrentInstance = Concurrent[F]

    concurrentInstance.par(fetchTally1,
      concurrentInstance.par(fetchTally2,
        concurrentInstance.par(fetchTally3,
          concurrentInstance.par(fetchTally4,
            concurrentInstance.par(fetchTally5,
              concurrentInstance.par(fetchTally6,
                concurrentInstance.par(fetchTally7,
                  concurrentInstance.par(fetchTally8,
                    concurrentInstance.par(fetchTally9,
                      fetchTally10))))))))).map {
      case
        (tally1,
        (tally2,
        (tally3,
        (tally4,
        (tally5,
        (tally6,
        (tally7,
        (tally8,
        (tally9,
        tally10,
          ))))))))) => (
        tally1,
        tally2,
        tally3,
        tally4,
        tally5,
        tally6,
        tally7,
        tally8,
        tally9,
        tally10,
      )
    }
  }

  def fetch8Tallies[F[+_, +_] : FetchTally : Concurrent, T1 <: Tally, T2 <: Tally, T3 <: Tally, T4 <: Tally, T5 <: Tally, T6 <: Tally, T7 <: Tally, T8 <: Tally]
  (
    fetchTally1: F[FetchTally.Error, T1],
    fetchTally2: F[FetchTally.Error, T2],
    fetchTally3: F[FetchTally.Error, T3],
    fetchTally4: F[FetchTally.Error, T4],
    fetchTally5: F[FetchTally.Error, T5],
    fetchTally6: F[FetchTally.Error, T6],
    fetchTally7: F[FetchTally.Error, T7],
    fetchTally8: F[FetchTally.Error, T8],
  ): F[FetchTally.Error, (T1, T2, T3, T4, T5, T6, T7, T8)] = {
    val concurrentInstance = Concurrent[F]

    concurrentInstance.par(fetchTally1,
      concurrentInstance.par(fetchTally2,
        concurrentInstance.par(fetchTally3,
          concurrentInstance.par(fetchTally4,
            concurrentInstance.par(fetchTally5,
              concurrentInstance.par(fetchTally6,
                concurrentInstance.par(fetchTally7,
                  fetchTally8))))))).map {
      case
        (tally1,
        (tally2,
        (tally3,
        (tally4,
        (tally5,
        (tally6,
        (tally7,
        tally8
          ))))))) => (
        tally1,
        tally2,
        tally3,
        tally4,
        tally5,
        tally6,
        tally7,
        tally8,
      )
    }
  }

  def fetch6Tallies[F[+_, +_] : FetchTally : Concurrent, T1 <: Tally, T2 <: Tally, T3 <: Tally, T4 <: Tally, T5 <: Tally, T6 <: Tally]
  (
    fetchTally1: F[FetchTally.Error, T1],
    fetchTally2: F[FetchTally.Error, T2],
    fetchTally3: F[FetchTally.Error, T3],
    fetchTally4: F[FetchTally.Error, T4],
    fetchTally5: F[FetchTally.Error, T5],
    fetchTally6: F[FetchTally.Error, T6],
  ): F[FetchTally.Error, (T1, T2, T3, T4, T5, T6)] = {
    val concurrentInstance = Concurrent[F]

    concurrentInstance.par(fetchTally1,
      concurrentInstance.par(fetchTally2,
        concurrentInstance.par(fetchTally3,
          concurrentInstance.par(fetchTally4,
            concurrentInstance.par(fetchTally5,
              fetchTally6))))).map {
      case
        (tally1,
        (tally2,
        (tally3,
        (tally4,
        (tally5,
        tally6))))) => (
        tally1,
        tally2,
        tally3,
        tally4,
        tally5,
        tally6,
      )
    }
  }

  def fetch4Tallies[F[+_, +_] : FetchTally : Concurrent, T1 <: Tally, T2 <: Tally, T3 <: Tally, T4 <: Tally]
  (
    fetchTally1: F[FetchTally.Error, T1],
    fetchTally2: F[FetchTally.Error, T2],
    fetchTally3: F[FetchTally.Error, T3],
    fetchTally4: F[FetchTally.Error, T4],
  ): F[FetchTally.Error, (T1, T2, T3, T4)] = {
    val concurrentInstance = Concurrent[F]

    concurrentInstance.par(fetchTally1,
      concurrentInstance.par(fetchTally2,
        concurrentInstance.par(fetchTally3,
          fetchTally4))).map {
      case
        (tally1,
        (tally2,
        (tally3,
        tally4))) => (
        tally1,
        tally2,
        tally3,
        tally4,
      )
    }
  }

  def fetch3Tallies[F[+_, +_] : FetchTally : Concurrent, T1 <: Tally, T2 <: Tally, T3 <: Tally]
  (
    fetchTally1: F[FetchTally.Error, T1],
    fetchTally2: F[FetchTally.Error, T2],
    fetchTally3: F[FetchTally.Error, T3],
  ): F[FetchTally.Error, (T1, T2, T3)] = {
    val concurrentInstance = Concurrent[F]

    concurrentInstance.par(fetchTally1,
      concurrentInstance.par(fetchTally2,
        fetchTally3)).map {
      case
        (tally1,
        (tally2,
        tally3)) => (
        tally1,
        tally2,
        tally3,
      )
    }
  }

  def fetch2Tallies[F[+_, +_] : FetchTally : Concurrent, T1 <: Tally, T2 <: Tally]
  (
    fetchTally1: F[FetchTally.Error, T1],
    fetchTally2: F[FetchTally.Error, T2],
  ): F[FetchTally.Error, (T1, T2)] = {
    val concurrentInstance = Concurrent[F]

    concurrentInstance.par(fetchTally1,
      fetchTally2).map {
      case
        (tally1,
        tally2) => (
        tally1,
        tally2,
      )
    }
  }

}
