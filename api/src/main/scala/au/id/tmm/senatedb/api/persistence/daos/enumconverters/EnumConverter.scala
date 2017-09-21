package au.id.tmm.senatedb.api.persistence.daos.enumconverters

private[daos] trait EnumConverter[A] {

  def apply(enumVal: A): String
  def apply(stringVal: String): A

}
