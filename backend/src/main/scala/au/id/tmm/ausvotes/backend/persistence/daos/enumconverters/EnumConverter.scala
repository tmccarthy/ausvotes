package au.id.tmm.ausvotes.backend.persistence.daos.enumconverters

private[daos] trait EnumConverter[A] {

  def apply(enumVal: A): String
  def apply(stringVal: String): A

}
