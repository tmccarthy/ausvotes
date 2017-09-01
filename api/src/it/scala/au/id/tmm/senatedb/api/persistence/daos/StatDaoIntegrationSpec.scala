package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.integrationtest.PostgresService
import au.id.tmm.senatedb.api.persistence.daos.StatDaoIntegrationSpec._
import au.id.tmm.senatedb.api.persistence.entities.stats.{Rank, Stat, StatClass}
import au.id.tmm.senatedb.core.fixtures.{DivisionFixture, PollingPlaceFixture}
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.senatedb.core.model.parsing.{Division, JurisdictionLevel, VoteCollectionPoint}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class StatDaoIntegrationSpec extends ImprovedFlatSpec with PostgresService {

  private val postcodeFlyweight = PostcodeFlyweight()
  private val addressDao = new ConcreteAddressDao(postcodeFlyweight)
  private val divisionDao = new ConcreteDivisionDao()
  private val vcpDao = new ConcreteVoteCollectionPointDao(addressDao, divisionDao, postcodeFlyweight)
  private val sut: StatDao = new ConcreteStatDao(postcodeFlyweight)

  private def insertTestData(): Unit = {
    Await.result(divisionDao.write(DivisionFixture.ACT.divisions), Duration.Inf)
    Await.result(vcpDao.write(Set(PollingPlaceFixture.ACT.BARTON) ++ Set(canberraPostalVcp1)), Duration.Inf)
    Await.result(sut.writeStats(SenateElection.`2016`, allTestStats), Duration.Inf)
  }

  "a stat dao" should "return all stats for an election" in {
    insertTestData()

    val actualStatsForElection = Await.result(sut.statsFor(SenateElection.`2016`), Duration.Inf)

    val expectedStatsForElection = Set(statForElection)

    assert(actualStatsForElection === expectedStatsForElection)
  }

  it should "return all stats for a state at an election" in {
    insertTestData()

    val actualStatsForState = Await.result(sut.statsFor(SenateElection.`2016`, State.ACT), Duration.Inf)

    val expectedStatsForState = Set(statForState)

    assert(actualStatsForState === expectedStatsForState)
  }

  it should "return all stats for a division" in {
    insertTestData()

    val actualStatsForDivision = Await.result(sut.statsFor(DivisionFixture.ACT.CANBERRA), Duration.Inf)

    val expectedStatsForDivision = Set(statForDivision)

    assert(actualStatsForDivision === expectedStatsForDivision)
  }

  it should "return all stats for a polling place" in {
    insertTestData()

    val actualStatsForPollingPlace = Await.result(sut.statsFor(PollingPlaceFixture.ACT.BARTON), Duration.Inf)

    val expectedStatsForPollingPlace = Set(statForPollingPlace)

    assert(actualStatsForPollingPlace === expectedStatsForPollingPlace)
  }

  it should "return all stats for a special VCP" in {
    insertTestData()

    val actualStatsForSpecialVcp = Await.result(sut.statsFor(canberraPostalVcp1), Duration.Inf)

    val expectedStatsForSpecialVcp = Set(statForSpecialVcp)

    assert(actualStatsForSpecialVcp === expectedStatsForSpecialVcp)
  }

  it should "indicate if at least one stat exists for each of a set of stat classes" in {
    insertTestData()

    val statClasses: Set[StatClass] = Set(StatClass.FormalBallots)

    val actual = Await.result(sut.hasSomeStatsForEachOf(SenateElection.`2016`, statClasses), Duration.Inf)

    assert(actual === true)
  }

  it should "indicate if at least one of a set of stat classes has no stats" in {
    insertTestData()

    val statClasses: Set[StatClass] = Set(StatClass.FormalBallots, StatClass.DonkeyVotes)

    val actual = Await.result(sut.hasSomeStatsForEachOf(SenateElection.`2016`, statClasses), Duration.Inf)

    assert(actual === false)
  }

}

object StatDaoIntegrationSpec {

  val canberraPostalVcp1 = VoteCollectionPoint.Postal(
    SenateElection.`2016`,
    state = State.ACT,
    division = DivisionFixture.ACT.CANBERRA,
    number = 1,
  )

  val statForElection: Stat[SenateElection] = Stat(
    statClass = StatClass.FormalBallots,
    jurisdictionLevel = JurisdictionLevel.Nation,
    jurisdiction = SenateElection.`2016`,
    amount = 42d,
    rankPerJurisdictionLevel = Map(),
    perCapita = Some(1d),
    rankPerCapitaPerJurisdictionLevel = Map(),
  )

  val statForState: Stat[State] = Stat(
    statClass = StatClass.FormalBallots,
    jurisdictionLevel = JurisdictionLevel.State,
    jurisdiction = State.ACT,
    amount = 42d,
    rankPerJurisdictionLevel = Map(
      JurisdictionLevel.Nation -> Rank(0, 8),
    ),
    perCapita = Some(1d),
    rankPerCapitaPerJurisdictionLevel = Map(
      JurisdictionLevel.Nation -> Rank(0, 8),
    ),
  )

  val statForDivision: Stat[Division] = Stat(
    statClass = StatClass.FormalBallots,
    jurisdictionLevel = JurisdictionLevel.Division,
    jurisdiction = DivisionFixture.ACT.CANBERRA,
    amount = 42d,
    rankPerJurisdictionLevel = Map(
      JurisdictionLevel.Nation -> Rank(4, 100),
      JurisdictionLevel.State -> Rank(2, 25),
    ),
    perCapita = Some(1d),
    rankPerCapitaPerJurisdictionLevel = Map(
      JurisdictionLevel.Nation -> Rank(0, 100),
      JurisdictionLevel.State -> Rank(0, 25),
    ),
  )

  val statForPollingPlace: Stat[VoteCollectionPoint] = Stat(
    statClass = StatClass.FormalBallots,
    jurisdictionLevel = JurisdictionLevel.VoteCollectionPoint,
    jurisdiction = PollingPlaceFixture.ACT.BARTON,
    amount = 42d,
    rankPerJurisdictionLevel = Map(
      JurisdictionLevel.Nation -> Rank(16, 300),
      JurisdictionLevel.State -> Rank(8, 80),
      JurisdictionLevel.Division -> Rank(2, 15),
    ),
    perCapita = Some(1d),
    rankPerCapitaPerJurisdictionLevel = Map(
      JurisdictionLevel.Nation -> Rank(0, 300),
      JurisdictionLevel.State -> Rank(0, 80),
      JurisdictionLevel.Division -> Rank(0, 15),
    ),
  )

  val statForSpecialVcp: Stat[VoteCollectionPoint] = Stat(
    statClass = StatClass.FormalBallots,
    jurisdictionLevel = JurisdictionLevel.VoteCollectionPoint,
    jurisdiction = canberraPostalVcp1,
    amount = 42d,
    rankPerJurisdictionLevel = Map(
      JurisdictionLevel.Nation -> Rank(17, 300),
      JurisdictionLevel.State -> Rank(9, 80),
      JurisdictionLevel.Division -> Rank(3, 15),
    ),
    perCapita = Some(1d),
    rankPerCapitaPerJurisdictionLevel = Map(
      JurisdictionLevel.Nation -> Rank(0, 300),
      JurisdictionLevel.State -> Rank(0, 80),
      JurisdictionLevel.Division -> Rank(0, 15),
    ),
  )

  val allTestStats: Set[Stat[_]] = Set(
    statForElection,
    statForState,
    statForDivision,
    statForPollingPlace,
    statForSpecialVcp,
  )
}