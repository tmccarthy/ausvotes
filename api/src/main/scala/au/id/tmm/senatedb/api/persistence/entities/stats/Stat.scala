package au.id.tmm.senatedb.api.persistence.entities.stats

import au.id.tmm.senatedb.core.model.parsing.JurisdictionLevel

final case class Stat[+A](statClass: StatClass,
                          jurisdictionLevel: JurisdictionLevel[A],
                          jurisdiction: A,
                          amount: Double,
                          rankPerJurisdictionLevel: Map[JurisdictionLevel[Any], Rank],
                          perCapita: Option[Double],
                          rankPerCapitaPerJurisdictionLevel: Map[JurisdictionLevel[Any], Rank]
                         )
