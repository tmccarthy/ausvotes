package au.id.tmm.ausvotes.data_sources.nswec.legco

import au.id.tmm.ausvotes.model.nsw.NswElection
import au.id.tmm.ausvotes.model.nsw.legco._
import au.id.tmm.ausvotes.model.stv.StvNormalisationRules
import au.id.tmm.countstv.normalisation.{BallotNormalisationRule, BallotNormalisationRules}

object CountRules {
  def normalisationRulesFor(election: NswLegCoElection): StvNormalisationRules =
    if (election.stateElection.date isBefore NswElection.`2019`.date) {
      // TODO make this correct
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
          BallotNormalisationRule.MinimumPreferences(15),
        )),
        btlOptionalRules = BallotNormalisationRules(Set(
          BallotNormalisationRule.MinimumPreferences(15),
          BallotNormalisationRule.CountingErrorsForbidden,
          BallotNormalisationRule.TicksForbidden,
          BallotNormalisationRule.CrossesForbidden,
        )),
      )
    }
}
