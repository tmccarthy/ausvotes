package au.id.tmm.senatedb.api.persistence.entities.stats

import au.id.tmm.senatedb.core.model.parsing.JurisdictionLevel
import au.id.tmm.utilities.collection.Rank

final case class Stat[+A](statClass: StatClass,
                          jurisdictionLevel: JurisdictionLevel[A],
                          jurisdiction: A,
                          amount: Double,
                          rankPerJurisdictionLevel: Map[JurisdictionLevel[Any], Rank],
                          perCapita: Option[Double],
                          rankPerCapitaPerJurisdictionLevel: Map[JurisdictionLevel[Any], Rank]
                         )
