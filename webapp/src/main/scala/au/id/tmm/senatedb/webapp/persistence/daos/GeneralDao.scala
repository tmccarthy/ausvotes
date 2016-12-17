package au.id.tmm.senatedb.webapp.persistence.daos

import com.google.inject.{ImplementedBy, Inject, Singleton}

@Singleton
@ImplementedBy(classOf[ConcreteGeneralDao])
trait GeneralDao {

  def divisionDao: DivisionDao

  def voteCollectionPointDao: VoteCollectionPointDao

}

class ConcreteGeneralDao @Inject() (val divisionDao: DivisionDao,
                                    val voteCollectionPointDao: VoteCollectionPointDao) extends GeneralDao {
}