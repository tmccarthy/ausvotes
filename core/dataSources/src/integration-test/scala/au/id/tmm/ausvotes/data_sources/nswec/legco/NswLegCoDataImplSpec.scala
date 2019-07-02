package au.id.tmm.ausvotes.data_sources.nswec.legco

import au.id.tmm.ausvotes.model.nsw.NswElection
import au.id.tmm.ausvotes.model.nsw.legco.NswLegCoElection
import au.id.tmm.bfect.fs2interop._
import au.id.tmm.bfect.ziointerop._
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}
import scalaz.zio.{DefaultRuntime, IO}

class NswLegCoDataImplSpec extends ImprovedFlatSpec with NeedsCleanDirectory with DefaultRuntime {

  private val sut = new NswLegCoDataImpl[IO](cleanDirectory, replaceExisting = true)

  "reading the groups and candidates of the nsw legislative assembly" should "get the correct number of candidates" in {
    val logicUnderTest = sut.fetchGroupsAndCandidatesFor(NswLegCoElection(NswElection.`2019`))

    val result = unsafeRunSync(logicUnderTest).toEither

    assert(result.map(_.candidates.size) === Right(346))
  }

  "reading the groups and candidates of the nsw legislative assembly" should "get the correct number of groups" in {
    val logicUnderTest = sut.fetchGroupsAndCandidatesFor(NswLegCoElection(NswElection.`2019`))

    val result = unsafeRunSync(logicUnderTest).toEither

    assert(result.map(_.groups.size) === Right(20))
  }

}
