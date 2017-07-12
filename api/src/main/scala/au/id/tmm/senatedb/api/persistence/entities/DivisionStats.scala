package au.id.tmm.senatedb.api.persistence.entities

import au.id.tmm.senatedb.core.model.parsing.Division

final case class DivisionStats (division: Division,

                                totalFormalBallots: TotalFormalBallotsTally
                               )
