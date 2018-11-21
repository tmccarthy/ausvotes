package au.id.tmm.ausvotes.core.model

import au.id.tmm.ausvotes.core.model.SenateElection.`2016`
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.geo.australia.State._

object IneligibleCandidates {

  def ineligibleCandidatesFor(election: SenateElection, state: State): Set[AecCandidateId] =
    (election, state) match {
      case (`2016`, NSW) => Set(
        AecCandidateId("28480"), // Fiona Nash
      )
      case (`2016`, VIC) => Set.empty
      case (`2016`, QLD) => Set(
        AecCandidateId("29442"), // Larissa Waters
        AecCandidateId("29470"), // Malcolm Roberts
      )
      case (`2016`, WA ) => Set(
        AecCandidateId("29186"), // Rod Culleton
        AecCandidateId("28246"), // Scott Ludlam
      )
      case (`2016`, SA ) => Set(
        AecCandidateId("29507"), // Skye Kakoschke-Moore
      )
      case (`2016`, TAS) => Set(
        AecCandidateId("28361"), // Jacqui Lambie
        AecCandidateId("28581"), // Stephen Parry
      )
      case (`2016`, ACT) => Set(
        AecCandidateId("28147"), // Katy Gallagher
      )
      case (`2016`, NT ) => Set.empty
      case _             => Set.empty
    }

}
