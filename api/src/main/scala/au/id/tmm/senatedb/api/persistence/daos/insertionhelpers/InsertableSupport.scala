package au.id.tmm.senatedb.api.persistence.daos.insertionhelpers

object InsertableSupport {
  type Insertable = Seq[(Symbol, Any)]
}