package au.id.tmm.senatedb.api.persistence.daos.enumconverters

import au.id.tmm.senatedb.api.persistence.entities.stats.StatClass
import com.google.common.collect.ImmutableBiMap

private[daos] object StatClassEnumConverter extends EnumConverter[StatClass] {

  // TODO this should be generated
  private val lookup: ImmutableBiMap[StatClass, String] = ImmutableBiMap.builder[StatClass, String]()
    .put(StatClass.FormalBallots, "FormalBallots")
    .put(StatClass.DonkeyVotes, "DonkeyVotes")
    .put(StatClass.VotedAtl, "VotedAtl")
    .put(StatClass.VotedAtlAndBtl, "VotedAtlAndBtl")
    .put(StatClass.VotedBtl, "VotedBtl")
    .put(StatClass.ExhaustedBallots, "ExhaustedBallots")
    .put(StatClass.ExhaustedVotes, "ExhaustedVotes")
    .put(StatClass.UsedHowToVoteCard, "UsedHowToVoteCard")
    .put(StatClass.Voted1Atl, "Voted1Atl")
    .put(StatClass.UsedSavingsProvision, "UsedSavingsProvision")
    .build()


  override def apply(enumVal: StatClass): String = lookup.get(enumVal)

  override def apply(stringVal: String): StatClass = lookup.inverse().get(stringVal)
}
