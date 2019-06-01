package au.id.tmm.ausvotes.shared.io.test.testdata

import java.time._

import au.id.tmm.bfect.testing.BState

final case class TimerTestData(
                              initialTime: Instant,
                              stepEachInvocation: Duration,
                            ) {
  def incrementBy(duration: Duration): TimerTestData = this.copy(initialTime = initialTime plus duration)
  def increment: TimerTestData = this.incrementBy(stepEachInvocation)

}

object TimerTestData {

  val default = TimerTestData(
    initialTime = Instant.EPOCH,
    stepEachInvocation = Duration.ofSeconds(1),
  )

  trait TestIOInstance[D] extends BState.TimerInstance[D] {
    protected def currentTimeField(data: D): TimerTestData
    protected def setCurrentTimeField(oldData: D, newCurrentTestTimeData: TimerTestData): D

    override def nowFromState(data: D): (D, Instant) = {
      val instantToReturn = currentTimeField(data).initialTime

      val newCurrentTimeTestData = currentTimeField(data).increment

      (setCurrentTimeField(data, newCurrentTimeTestData), instantToReturn)
    }

    override def applySleepToState(sleepDuration: Duration, data: D): D = {
      val newTestData = currentTimeField(data).incrementBy(sleepDuration)

      setCurrentTimeField(data, newTestData)
    }

  }

}
