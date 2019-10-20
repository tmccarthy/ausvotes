package au.id.tmm.ausvotes.core.model

import au.id.tmm.ausvotes.model.CandidateDetails
import au.id.tmm.ausvotes.model.federal.senate.SenateElection.`2016`
import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import au.id.tmm.ausgeo.State._

// TODO move this to the data_sources
object IneligibleCandidates {

  def ineligibleCandidatesFor(election: SenateElectionForState): Set[CandidateDetails.Id] =
    election match {
      case SenateElectionForState(`2016`, NSW) => Set(
        CandidateDetails.Id(28480), // Fiona Nash
      )
      case SenateElectionForState(`2016`, VIC) => Set.empty
      case SenateElectionForState(`2016`, QLD) => Set(
        CandidateDetails.Id(29442), // Larissa Waters
        CandidateDetails.Id(29470), // Malcolm Roberts
      )
      case SenateElectionForState(`2016`, WA ) => Set(
        CandidateDetails.Id(29186), // Rod Culleton
        CandidateDetails.Id(28246), // Scott Ludlam
      )
      case SenateElectionForState(`2016`, SA ) => Set(
        CandidateDetails.Id(29507), // Skye Kakoschke-Moore
      )
      case SenateElectionForState(`2016`, TAS) => Set(
        CandidateDetails.Id(28361), // Jacqui Lambie
        CandidateDetails.Id(28581), // Stephen Parry
      )
      case SenateElectionForState(`2016`, ACT) => Set(
        CandidateDetails.Id(28147), // Katy Gallagher
      )
      case SenateElectionForState(`2016`, NT ) => Set.empty
      case _             => Set.empty
    }

}
