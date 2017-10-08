package au.id.tmm.ausvotes.api.persistence.daos.enumconverters

import au.id.tmm.ausvotes.core.model.parsing.JurisdictionLevel
import com.google.common.collect.ImmutableBiMap

private[daos] object JurisdictionLevelEnumConverter extends EnumConverter[JurisdictionLevel[_]] {

  private val lookup = ImmutableBiMap.builder[JurisdictionLevel[_], String]()
    .put(JurisdictionLevel.Nation, "Nation")
    .put(JurisdictionLevel.State, "State")
    .put(JurisdictionLevel.Division, "Division")
    .put(JurisdictionLevel.VoteCollectionPoint, "VoteCollectionPoint")
    .build()

  override def apply(enumVal: JurisdictionLevel[_]): String = lookup.get(enumVal)

  override def apply(stringVal: String): JurisdictionLevel[_] = lookup.inverse.get(stringVal)
}
