package au.id.tmm.ausvotes.data_sources.aec.federal.extras

import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.ausvotes.model.stv.StvNormalisationRules
import au.id.tmm.countstv.normalisation.{BallotNormalisationRule, BallotNormalisationRules}

object CountRules {
  def normalisationRulesFor(senateElection: SenateElection): StvNormalisationRules =
    if (senateElection.date isBefore SenateElection.`2016`.date) {
      StvNormalisationRules(
        BallotNormalisationRules.laxest,
        BallotNormalisationRules.laxest,
        BallotNormalisationRules.laxest,
        BallotNormalisationRules.laxest,
      )
    } else {
      StvNormalisationRules(
        atlMandatoryRules = BallotNormalisationRules(Set(
          BallotNormalisationRule.MinimumPreferences(1),
        )),
        atlOptionalRules = BallotNormalisationRules(Set(
          BallotNormalisationRule.MinimumPreferences(6),
          BallotNormalisationRule.CountingErrorsForbidden,
          BallotNormalisationRule.TicksForbidden,
          BallotNormalisationRule.CrossesForbidden,
        )),
        btlMandatoryRules = BallotNormalisationRules(Set(
          BallotNormalisationRule.MinimumPreferences(6),
        )),
        btlOptionalRules = BallotNormalisationRules(Set(
          BallotNormalisationRule.MinimumPreferences(12),
          BallotNormalisationRule.CountingErrorsForbidden,
          BallotNormalisationRule.TicksForbidden,
          BallotNormalisationRule.CrossesForbidden,
        )),
      )
    }
}
