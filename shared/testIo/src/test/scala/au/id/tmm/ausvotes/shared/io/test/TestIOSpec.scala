package au.id.tmm.ausvotes.shared.io.test

import java.io.IOException

import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class TestIOSpec extends ImprovedFlatSpec {

  "catchLeft" can "handle an error" in {
    val beforeCatch: BasicTestData.TestIO[Exception, Nothing] = TestIO.leftPure(new IllegalArgumentException)

    val afterCatch: BasicTestData.TestIO[Exception, Int] = beforeCatch.catchLeft {
      case _: IllegalArgumentException => TestIO.pure(5)
    }

    val (_, result) = afterCatch.run(BasicTestData())

    assert(result === Right(5))
  }

  it can "retain an error" in {
    val actualIllegalArgumentException = new IllegalArgumentException
    val beforeCatch: BasicTestData.TestIO[Exception, Nothing] = TestIO.leftPure(actualIllegalArgumentException)

    val afterCatch: BasicTestData.TestIO[Exception, Int] = beforeCatch.catchLeft {
      case _: IOException => TestIO.pure(5)
    }

    val (_, result) = afterCatch.run(BasicTestData())

    assert(result === Left(actualIllegalArgumentException))
  }

  it should "not alter a successful value" in {
    val beforeCatch: BasicTestData.TestIO[Exception, Int] = TestIO.pure(5)

    val afterCatch: BasicTestData.TestIO[Exception, Int] = beforeCatch.catchLeft {
      case _: IOException => TestIO.pure(10)
    }

    val (_, result) = afterCatch.run(BasicTestData())

    assert(result === Right(5))
  }

}
