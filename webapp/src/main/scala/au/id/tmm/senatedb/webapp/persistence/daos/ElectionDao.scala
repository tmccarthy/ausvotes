package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import com.google.common.collect.ImmutableBiMap
import com.google.inject.{ImplementedBy, Singleton}

import scala.concurrent.Future

// TODO the election table in the db should be populated from the enum
@ImplementedBy(classOf[HardCodedElectionDao])
trait ElectionDao {
  def electionWithId(electionId: String): Future[Option[SenateElection]]

  def electionWithIdBlocking(electionId: String): Option[SenateElection]

  def idOfBlocking(election: SenateElection): Option[String]
}

// TODO replace this with something that goes to the db?
@Singleton
class HardCodedElectionDao extends ElectionDao {

  // TODO needs scala implementation
  private val electionIdLookup: ImmutableBiMap[SenateElection, String] = ImmutableBiMap.of(
    SenateElection.`2016`, "2016",
    SenateElection.`2014 WA`, "2014WA",
    SenateElection.`2013`, "2013"
  )

  override def electionWithId(electionId: String): Future[Option[SenateElection]] = {
    val election = electionWithIdBlocking(electionId)

    Future.successful(election)
  }

  override def electionWithIdBlocking(electionId: String): Option[SenateElection] = {
    Option(electionIdLookup.inverse.get(electionId.toUpperCase))
  }

  override def idOfBlocking(election: SenateElection): Option[String] = {
    Option(electionIdLookup.get(election))
  }
}