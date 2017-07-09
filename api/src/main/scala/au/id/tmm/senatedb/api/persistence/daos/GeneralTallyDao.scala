package au.id.tmm.senatedb.api.persistence.daos

import com.google.inject.{ImplementedBy, Inject}

@ImplementedBy(classOf[ConcreteGeneralTallyDao])
trait GeneralTallyDao {

  def totalFormalBallotsDao: TotalFormalBallotsDao

}

class ConcreteGeneralTallyDao @Inject() (val totalFormalBallotsDao: TotalFormalBallotsDao) extends GeneralTallyDao {

}