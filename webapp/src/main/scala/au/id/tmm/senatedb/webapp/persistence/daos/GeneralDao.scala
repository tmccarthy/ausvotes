package au.id.tmm.senatedb.webapp.persistence.daos

import com.google.inject.{ImplementedBy, Inject, Singleton}

@Singleton
@ImplementedBy(classOf[ConcreteGeneralDao])
trait GeneralDao {

  def divisionDao: DivisionDao

  def voteCollectionPointDao: VoteCollectionPointDao

  def addressDao: AddressDao

  def generalTallyDao: GeneralTallyDao

}

class ConcreteGeneralDao @Inject() (val divisionDao: DivisionDao,
                                    val voteCollectionPointDao: VoteCollectionPointDao,
                                    val generalTallyDao: GeneralTallyDao,
                                    val addressDao: AddressDao
                                   ) extends GeneralDao {
}