package au.id.tmm.senatedb.api.persistence.daos.insertionhelpers

import au.id.tmm.senatedb.api.persistence.daos.enumconverters.{ElectionEnumConverter, StatClassEnumConverter, StateEnumConverter}
import au.id.tmm.senatedb.api.persistence.daos.insertionhelpers.InsertableSupport.Insertable
import au.id.tmm.senatedb.api.persistence.entities.stats.Stat
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint.SpecialVoteCollectionPoint
import au.id.tmm.senatedb.core.model.parsing.{Division, JurisdictionLevel, PollingPlace, VoteCollectionPoint}
import au.id.tmm.utilities.geo.australia.State

private[daos] object StatInsertableHelper {

  def toInsertable(election: SenateElection, stat: Stat[_]): Insertable = {
    val jurisdictionFields: Seq[(Symbol, Any)] = stat.jurisdictionLevel match {
      case JurisdictionLevel.Nation => Seq(
        'state -> null,
        'division -> null,
        'special_vcp -> null,
        'polling_place -> null,
      )
      case JurisdictionLevel.State => Seq(
        'state -> StateEnumConverter(stat.jurisdiction.asInstanceOf[State]),
        'division -> null,
        'special_vcp -> null,
        'polling_place -> null,
      )
      case JurisdictionLevel.Division => {
        val division = stat.jurisdiction.asInstanceOf[Division]
        Seq(
          'state -> StateEnumConverter(division.state),
          'division -> DivisionInsertableHelper.idOf(division),
          'special_vcp -> null,
          'polling_place -> null,
        )
      }
      case JurisdictionLevel.VoteCollectionPoint => {
        val vcp = stat.jurisdiction.asInstanceOf[VoteCollectionPoint]

        val vcpFields = vcp match {
          case v: SpecialVoteCollectionPoint => Seq(
            'special_vcp -> SpecialVcpInsertableHelper.idOf(v),
            'polling_place -> null,
          )
          case p: PollingPlace => Seq(
            'special_vcp -> null,
            'polling_place -> PollingPlaceInsertableHelper.idOf(p),
          )
        }

        Seq(
          'state -> StateEnumConverter(vcp.state),
          'division -> DivisionInsertableHelper.idOf(vcp.division),
        ) ++ vcpFields
      }
    }

    Seq(
      'stat_class -> StatClassEnumConverter(stat.statClass),
      'election -> ElectionEnumConverter(election),
    ) ++ jurisdictionFields ++ Seq(
      'amount -> stat.amount,
      'per_capita -> stat.perCapita,
    )
  }

  def toInsertableTuple(election: SenateElection, stat: Stat[_]): Seq[Any] = {
    toInsertable(election, stat).map { case (symbol, value) =>
      value
    }
  }

}
