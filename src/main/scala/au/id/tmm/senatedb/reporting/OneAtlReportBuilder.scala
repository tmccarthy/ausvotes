package au.id.tmm.senatedb.reporting
import au.id.tmm.senatedb.model.parsing.{BallotGroup, Division, Party}
import au.id.tmm.senatedb.tallies.CountOneAtl
import au.id.tmm.senatedb.tallies.Tallier.{NormalTallier, SimpleTallier, TieredTallier}
import au.id.tmm.utilities.geo.australia.State

object OneAtlReportBuilder extends StandardPredicateBasedReportBuilder {

  override def reportTitle: String = "Ballots with only '1' above the line"

  override def primaryCountColumnTitle: String = "Ballots with only '1' above the line"

  override def nationalTallier: SimpleTallier = CountOneAtl.Nationally

  override def nationalPerFirstPreferenceTallier: NormalTallier[Party] = CountOneAtl.NationallyByFirstPreference

  override def perStateTallier: NormalTallier[State] = CountOneAtl.ByState

  override def perDivisionTallier: NormalTallier[Division] = CountOneAtl.ByDivision

  override def perFirstPreferencedGroupTallier: TieredTallier[State, BallotGroup] = CountOneAtl.ByFirstPreferencedGroup

}
