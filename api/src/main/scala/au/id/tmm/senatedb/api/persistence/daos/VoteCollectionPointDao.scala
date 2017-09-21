package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.persistence.daos.enumconverters.ElectionEnumConverter
import au.id.tmm.senatedb.api.persistence.daos.insertionhelpers.InsertableSupport.Insertable
import au.id.tmm.senatedb.api.persistence.daos.insertionhelpers.{DivisionInsertableHelper, PollingPlaceInsertableHelper, SpecialVcpInsertableHelper}
import au.id.tmm.senatedb.api.persistence.daos.rowentities._
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.senatedb.core.model.parsing.PollingPlace.Location.{Multiple, Premises, PremisesMissingLatLong}
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint._
import au.id.tmm.senatedb.core.model.parsing.{Division, PollingPlace, VoteCollectionPoint}
import com.google.inject.{ImplementedBy, Inject, Singleton}
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ConcreteVoteCollectionPointDao])
trait VoteCollectionPointDao {
  def write(voteCollectionPoints: Iterable[VoteCollectionPoint]): Future[Unit]

  def hasAnySpecialVoteCollectionPointsFor(election: SenateElection): Future[Boolean]

  def hasAnyPollingPlacesFor(election: SenateElection): Future[Boolean]

  def allForDivision(division: Division): Future[Set[VoteCollectionPoint]]

  def allPollingPlacesForDivision(division: Division): Future[Set[PollingPlace]]

  def allSpecialVoteCollectionPointsForDivision(division: Division): Future[Set[SpecialVoteCollectionPoint]]

  def idOf(pollingPlace: PollingPlace): Long

  def idOf(specialVoteCollectionPoint: SpecialVoteCollectionPoint): Long
}

@Singleton
class ConcreteVoteCollectionPointDao @Inject() (addressDao: AddressDao,
                                                divisionDao: DivisionDao,
                                                postcodeFlyweight: PostcodeFlyweight)
                                               (implicit ec: ExecutionContext) extends VoteCollectionPointDao {

  override def write(voteCollectionPoints: Iterable[VoteCollectionPoint]): Future[Unit] = Future {
    DB.localTx { implicit session =>
      writeBatch(voteCollectionPoints.toVector)
    }
  }

  private def writeBatch(vcps: Vector[VoteCollectionPoint])(implicit session: DBSession): Unit = {
    val (pollingPlaces, specialVcps) = vcps.partition {
      case _: PollingPlace => true
      case _: SpecialVoteCollectionPoint => false
    }

    writePollingPlaceBatch(pollingPlaces.asInstanceOf[Vector[PollingPlace]])
    writeSpecialVcpBatch(specialVcps.asInstanceOf[Vector[SpecialVoteCollectionPoint]])
  }

  private def writePollingPlaceBatch(pollingPlaces: Vector[PollingPlace])(implicit session: DBSession): Unit = {
    val addressesToWrite = pollingPlaces.map(_.location).flatMap {
      case Multiple => None
      case Premises(_, address, _) => Some(address)
      case PremisesMissingLatLong(_, address) => Some(address)
    }

    val idsPerAddress = addressDao.writeInSession(addressesToWrite)

    val insertStatement =
      sql"""
           |INSERT INTO polling_place(id, election, state, division, aec_id, polling_place_type, name, multiple_locations, premises_name, address, latitude, longitude)
           |  VALUES (
           |  {id},
           |  {election},
           |  {state},
           |  {division},
           |  {aec_id},
           |  CAST({polling_place_type} AS polling_place_type),
           |  {name},
           |  {multiple_locations},
           |  {premises_name},
           |  {address},
           |  {latitude},
           |  {longitude}
           |)
           |""".stripMargin

    val toInsert: Vector[Insertable] = pollingPlaces.map(PollingPlaceInsertableHelper.toInsertable(idsPerAddress))

    insertStatement.batchByName(toInsert: _*).apply()
  }

  private def writeSpecialVcpBatch(specialVcps: Vector[SpecialVoteCollectionPoint])(implicit session: DBSession): Unit = {
    val insertStatement =
      sql"""
           |INSERT INTO special_vote_collection_point(id, election, state, division, vote_collection_point_type, name, number)
           |  VALUES ({id}, {election}, {state}, {division}, CAST({vote_collection_point_type} AS vote_collection_point_type), {name}, {number});
           |""".stripMargin

    val toInsert: Vector[Insertable] = specialVcps.map(SpecialVcpInsertableHelper.toInsertable)

    insertStatement.batchByName(toInsert: _*).apply()
  }

  override def hasAnyPollingPlacesFor(election: SenateElection): Future[Boolean] = Future {
    DB.localTx { implicit session =>

      val p = PollingPlaceRow.syntax

      withSQL {
        select(p.id)
          .from(PollingPlaceRow as p)
          .limit(1)
      }
        .map(_.long(1))
        .first()
        .apply()
        .isDefined
    }
  }

  override def hasAnySpecialVoteCollectionPointsFor(election: SenateElection): Future[Boolean] = Future {
    DB.localTx { implicit session =>
      val v = SpecialVcpRow.syntax

      withSQL {
        select(v.id)
          .from(SpecialVcpRow as v)
          .limit(1)
      }
        .map(_.long(1))
        .first()
        .apply()
        .isDefined
    }
  }

  override def allForDivision(division: Division): Future[Set[VoteCollectionPoint]] = {
    val eventualPollingPlaces = allPollingPlacesForDivision(division)
    val eventualSpecialVoteCollectionPoints = allSpecialVoteCollectionPointsForDivision(division)

    for {
      pollingPlaces <- eventualPollingPlaces
      specialVoteCollectionPoints <- eventualSpecialVoteCollectionPoints
    } yield pollingPlaces ++ specialVoteCollectionPoints
  }

  override def allPollingPlacesForDivision(division: Division): Future[Set[PollingPlace]] = Future {
    DB.localTx { implicit session =>

      val (p, d, a) = (PollingPlaceRow.syntax, DivisionRow.syntax, AddressRow.syntax)

      withSQL {
        select
          .from(PollingPlaceRow as p)
          .leftJoin(DivisionRow as d).on(p.division, d.id)
          .leftJoin(AddressRow as a).on(p.address, a.id)
          .where
          .eq(p.division, DivisionInsertableHelper.idOf(division))
          .and
          .eq(p.election, ElectionEnumConverter(division.election))
      }
        .map(PollingPlaceRow(postcodeFlyweight, p, d, a))
        .traversable()
        .apply()
        .map(_.asVoteCollectionPoint)
        .toSet
    }
  }

  override def allSpecialVoteCollectionPointsForDivision(division: Division): Future[Set[SpecialVoteCollectionPoint]] = Future {
    DB.localTx { implicit session =>
      val (v, d) = (SpecialVcpRow.syntax, DivisionRow.syntax)

      withSQL {
        select
          .from(SpecialVcpRow as v)
          .leftJoin(DivisionRow as d).on(v.division, d.id)
          .where
          .eq(v.division, DivisionInsertableHelper.idOf(division))
          .and
          .eq(v.election, ElectionEnumConverter(division.election))
      }
        .map(SpecialVcpRow(v, d))
        .traversable()
        .apply()
        .map(_.asVoteCollectionPoint)
        .toSet
    }
  }

  override def idOf(specialVcp: SpecialVoteCollectionPoint): Long = SpecialVcpInsertableHelper.idOf(specialVcp)

  override def idOf(pollingPlace: PollingPlace): Long = PollingPlaceInsertableHelper.idOf(pollingPlace)
}
