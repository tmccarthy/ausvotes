package au.id.tmm.ausvotes.core.computations.parties

import au.id.tmm.ausvotes.model.Party

object PartyEquivalenceComputation {

  def nationalEquivalentOf(party: Party): Party = party match {
    case Party.ALPNTBranch => Party.ALP
    case Party.GreensWA => Party.Greens
    case Party.CountryLiberalsNT |
         Party.LNP |
         Party.LiberalWithNationals => Party.Liberal
    case p => p
  }

  def isInCoalition(party: Party): Boolean = party match {
    case Party.Liberal |
         Party.Nationals |
         Party.CountryLiberalsNT |
         Party.LNP |
         Party.LiberalWithNationals => true
    case _ => false
  }

}
