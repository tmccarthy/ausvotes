package au.id.tmm.ausvotes.core.computations.parties

import au.id.tmm.ausvotes.core.computations.parties.PartyEquivalenceComputation.{isInCoalition, nationalEquivalentOf}
import au.id.tmm.ausvotes.model.PartySignificance._
import au.id.tmm.ausvotes.model.{Party, PartySignificance}

object PartySignificanceComputation {

  def of(party: Option[Party]): PartySignificance = party match {
    case Some(party) if nationalEquivalentOf(party) == Party.ALP    => Major
    case Some(party) if nationalEquivalentOf(party) == Party.Greens => Major
    case Some(party) if isInCoalition(party)                        => Major
    case Some(party)                                                => Minor
    case None                                                       => Independent
  }

}
