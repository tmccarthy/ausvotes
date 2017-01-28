package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.{Division, VoteCollectionPoint}
import au.id.tmm.senatedb.core.tallies.Tally
import au.id.tmm.senatedb.webapp.persistence.entities.{TallyOrdinalComputations, TotalFormalBallotsTally}
import au.id.tmm.utilities.geo.australia.State
import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.libs.concurrent.Execution.Implicits._
import scalikejdbc._

import scala.collection.immutable.Seq
import scala.concurrent.Future

@ImplementedBy(classOf[ConcreteTotalFormalBallotsDao])
trait TotalFormalBallotsDao {
  def hasTallyForAnyDivisionAt(election: SenateElection): Future[Boolean]

  def hasTallyForAnyVoteCollectionPointAt(election: SenateElection): Future[Boolean]

  def writePerDivision(tally: Tally[Division]): Future[Unit]

  def writePerVoteCollectionPoint(election: SenateElection, tally: Tally[VoteCollectionPoint]): Future[Unit]
}

@Singleton
class ConcreteTotalFormalBallotsDao @Inject() (electionDao: ElectionDao,
                                               divisionDao: DivisionDao,
                                               voteCollectionPointDao: VoteCollectionPointDao)
    extends TotalFormalBallotsDao {

  override def hasTallyForAnyDivisionAt(election: SenateElection): Future[Boolean] = Future {
    val electionId = electionDao.idOf(election).get

    DB.readOnly { implicit session =>
      val statement = sql"""SELECT *
           |FROM division
           |  LEFT JOIN division_stats ON division.id = division_stats.division
           |WHERE division.election = $electionId
           |    AND division_stats.total_formal_ballot_count_id IS NOT NULL""".stripMargin

      statement.map(_ => Unit)
        .first()
        .apply()
        .isDefined
    }
  }

  override def hasTallyForAnyVoteCollectionPointAt(election: SenateElection): Future[Boolean] = Future {
    val electionId = electionDao.idOf(election).get

    DB.readOnly { implicit session =>
      val statement = sql"""SELECT *
                         |FROM vote_collection_point
                         |  LEFT JOIN vote_collection_point_stats ON vote_collection_point.id = vote_collection_point_stats.vote_collection_point_id
                         |WHERE vote_collection_point.election = $electionId
                         |    AND vote_collection_point_stats.total_formal_ballot_count_id IS NOT NULL""".stripMargin

      statement.map(_ => Unit)
        .first()
        .apply()
        .isDefined
    }
  }

  override def writePerDivision(tally: Tally[Division]): Future[Unit] = Future {
    val talliesToWrite = TotalFormalBallotsRowConversions.toEntities[Division](tally, Some(_.state), None)

    DB.localTx { implicit session =>
      
      val idsPerWrittenTally = writeTotalFormalBallotTallies(talliesToWrite)

      val statsTableInsert =
        sql"""INSERT INTO division_stats (
             |  division,
             |  total_formal_ballot_count_id
             |) VALUES (
             |  {division},
             |  {total_formal_ballot_count_id}
             |)
             |ON CONFLICT (division) DO UPDATE
             |  SET total_formal_ballot_count_id = excluded.total_formal_ballot_count_id""".stripMargin

      val statsTableRows = idsPerWrittenTally
        .toStream
        .map {
          case (tallyForDivision, total_formal_ballot_count_id) =>
            Seq(
              Symbol("division") -> divisionDao.idOf(tallyForDivision.attachedEntity),
              Symbol("total_formal_ballot_count_id") -> total_formal_ballot_count_id
            )
        }

      statsTableInsert.batchByName(statsTableRows: _*)
        .apply()
    }
  }

  override def writePerVoteCollectionPoint(election: SenateElection,
                                           tally: Tally[VoteCollectionPoint]): Future[Unit] = Future {
    val talliesToWrite = TotalFormalBallotsRowConversions.toEntities[VoteCollectionPoint](tally, Some(_.state), Some(_.division))

    DB.localTx { implicit session =>

      val idsPerWrittenTally = writeTotalFormalBallotTallies(talliesToWrite)

      val idsPerVoteCollectionPoint: Map[VoteCollectionPoint, Long] =
        voteCollectionPointDao.idPerVoteCollectionPointInSession(election)

      val statsTableInsert =
        sql"""INSERT INTO vote_collection_point_stats (
           |  vote_collection_point_id,
           |  total_formal_ballot_count_id
           |) VALUES (
           |  {vote_collection_point_id},
           |  {total_formal_ballot_count_id}
           |)
           |ON CONFLICT (vote_collection_point_id) DO UPDATE
           |  SET total_formal_ballot_count_id = excluded.total_formal_ballot_count_id""".stripMargin

      val statsTableRows = idsPerWrittenTally
        .toStream
        .map {
          case (tallyForVcp, total_formal_ballot_count_id) =>
            Seq(
              Symbol("vote_collection_point_id") -> idsPerVoteCollectionPoint(tallyForVcp.attachedEntity),
              Symbol("total_formal_ballot_count_id") -> total_formal_ballot_count_id
            )
        }

      statsTableInsert.batchByName(statsTableRows: _*)
        .apply()
    }
  }

  private def writeTotalFormalBallotTallies[A](tallies: Set[TotalFormalBallotsTally[A]])
                                              (implicit session: DBSession): Map[TotalFormalBallotsTally[A], Long] = {
    tallies.toStream
      .map { tally =>
        val asRow = TotalFormalBallotsRowConversions.toRow(tally)

        val statement = sql"""INSERT INTO total_formal_ballot_count(
                             |  total_formal_ballots,
                             |  ordinal_nationally,
                             |  ordinal_state,
                             |  ordinal_division
                             |) VALUES (
                             |  ${asRow("total_formal_ballots")},
                             |  ${asRow("ordinal_nationally")},
                             |  ${asRow("ordinal_state")},
                             |  ${asRow("ordinal_division")}
                             |)""".stripMargin
          .updateAndReturnGeneratedKey()

        val generatedId = statement.apply()

        tally -> generatedId
      }
      .toMap
  }
}

private[daos] object TotalFormalBallotsRowConversions extends RowConversions {

  def fromRow[A](attachedEntityConverter: WrappedResultSet => A)(row: WrappedResultSet): TotalFormalBallotsTally[A] = {
    TotalFormalBallotsTally(
      attachedEntityConverter(row),
      row.long("total_formal_ballots"),
      row.int("ordinal_nationally"),
      Option(row.nullableInt("ordinal_state")),
      Option(row.nullableInt("ordinal_division"))
    )
  }

  def toEntities[A](tally: Tally[A],
                    computeState: Option[A => State],
                    computeDivision: Option[A => Division]): Set[TotalFormalBallotsTally[A]] = {

    val nationalOrdinalsPerJurisdiction: Map[A, Int] = TallyOrdinalComputations.ordinalNationally(tally)
    val stateOrdinalsPerJurisdiction: Option[Map[A, Int]] = computeState.map(TallyOrdinalComputations.ordinalWithinState(tally, _))
    val voteCollectionOrdinalsPerJurisdiction: Option[Map[A, Int]] = computeDivision.map(TallyOrdinalComputations.ordinalWithinDivision(tally, _))

    tally.values.toStream
      .map {
        case (jurisdiction, count) => TotalFormalBallotsTally(
          jurisdiction,
          count.toLong,
          nationalOrdinalsPerJurisdiction(jurisdiction),
          stateOrdinalsPerJurisdiction.map(_(jurisdiction)),
          voteCollectionOrdinalsPerJurisdiction.map(_(jurisdiction))
        )
      }
      .toSet
  }

  def toRow[A](entity: TotalFormalBallotsTally[A]): Map[String, Any] = {
    Map(
      "total_formal_ballots" -> entity.absoluteCount,
      "ordinal_nationally" -> entity.ordinalNationally,
      "ordinal_state" -> entity.ordinalInState.orNull,
      "ordinal_division" -> entity.ordinalInDivision.orNull
    )
  }
}