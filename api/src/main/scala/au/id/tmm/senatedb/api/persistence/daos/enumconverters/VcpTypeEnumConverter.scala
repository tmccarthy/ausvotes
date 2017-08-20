package au.id.tmm.senatedb.api.persistence.daos.enumconverters

import au.id.tmm.senatedb.api.persistence.daos.rowentities.VoteCollectionPointRow.VcpType

private[daos] object VcpTypeEnumConverter extends EnumConverter[VcpType] {
  override def apply(enumVal: VcpType): String = enumVal match {
    case VcpType.Absentee => "absentee"
    case VcpType.Postal => "postal"
    case VcpType.Prepoll => "prepoll"
    case VcpType.Provisional => "provisional"
    case VcpType.PollingPlace => "polling_place"
  }

  override def apply(stringVal: String): VcpType = stringVal match {
    case "absentee" => VcpType.Absentee
    case "postal" => VcpType.Postal
    case "prepoll" => VcpType.Prepoll
    case "provisional" => VcpType.Provisional
    case "polling_place" => VcpType.PollingPlace
  }
}
