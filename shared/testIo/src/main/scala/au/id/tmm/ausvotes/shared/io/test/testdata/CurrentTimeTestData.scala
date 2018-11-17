package au.id.tmm.ausvotes.shared.io.test.testdata

import java.time._

import au.id.tmm.ausvotes.shared.io.actions.Now
import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO.Output

final case class CurrentTimeTestData(
                                      initialTime: Instant,
                                      stepEachInvocation: Duration,
                                    ) {
  def increment: CurrentTimeTestData = this.copy(initialTime = initialTime plus stepEachInvocation)
}

object CurrentTimeTestData {

  val default = CurrentTimeTestData(
    initialTime = Instant.EPOCH,
    stepEachInvocation = Duration.ofSeconds(1),
  )

  def testIOInstance[D](
                         currentTimeField: D => CurrentTimeTestData,
                         setCurrentTimeField: (D, CurrentTimeTestData) => D,
                       ): Now[TestIO[D, +?, +?]] = new Now[TestIO[D, +?, +?]] {

    override def systemNanoTime: TestIO[D, Nothing, Long] = testIOWithTime(i => i.getEpochSecond * 1000000000 + i.getNano)
    override def currentTimeMillis: TestIO[D, Nothing, Long] = testIOWithTime(_.toEpochMilli)
    override def nowInstant: TestIO[D, Nothing, Instant] = testIOWithTime(identity)
    override def nowLocalDate: TestIO[D, Nothing, LocalDate] = testIOWithTime(LocalDate.from(_))
    override def nowZonedDateTime: TestIO[D, Nothing, ZonedDateTime] = testIOWithTime(ZonedDateTime.ofInstant(_, ZoneId.systemDefault()))

    private def testIOWithTime[A](fromInstant: Instant => A): TestIO[D, Nothing, A] =
      TestIO(data => {
        val instantToReturn = currentTimeField(data).initialTime

        val newCurrentTimeTestData = currentTimeField(data).increment

        Output(setCurrentTimeField(data, newCurrentTimeTestData), Right(fromInstant(instantToReturn)))
      })

  }

}
