package au.id.tmm.senatedb.webapp.persistence.entities

import au.id.tmm.senatedb.core.model.parsing.Division

final case class DivisionStats (totalFormalBallots: TotalFormalBallotsTally[Division])
