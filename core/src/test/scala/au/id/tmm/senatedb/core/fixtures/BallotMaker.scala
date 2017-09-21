package au.id.tmm.senatedb.core.fixtures

import au.id.tmm.senatedb.core.fixtures.CandidateFixture.CandidateFixture
import au.id.tmm.senatedb.core.model.parsing.Ballot.{AtlPreferences, BtlPreferences}
import au.id.tmm.senatedb.core.model.parsing._

case class BallotMaker(candidateFixture: CandidateFixture) {

  def makeBallot(atlPreferences: AtlPreferences = Map.empty,
                 btlPreferences: BtlPreferences = Map.empty,
                 division: Division = DivisionFixture.ACT.CANBERRA,
                 pollingPlace: PollingPlace = PollingPlaceFixture.ACT.BARTON,
                 batch: Int = 1,
                 paper: Int = 1
            ) = {
    Ballot(candidateFixture.election, candidateFixture.state, division, pollingPlace, batch, paper, atlPreferences, btlPreferences)
  }

  def orderedAtlPreferences(groupsInOrder: String*): AtlPreferences = {
    val preferencesPerGroup = groupsInOrder.zipWithIndex
      .map { case (group, index) => (group, (index + 1).toString) }

    atlPreferences(preferencesPerGroup: _*)
  }

  def atlPreferences(prefPerGroup: (String, String)*): AtlPreferences = {
    prefPerGroup.map {
      case (groupCode, rawPref) => candidateFixture.groupLookup(groupCode) -> Preference.fromRawValue(rawPref).get
    }.map {
      case (group: Group, preference) => group -> preference
      case (u: Ungrouped, preference) => throw new IllegalArgumentException("Can't have Ungrouped above the line")
    }.toMap
  }

  def orderedBtlPreferences(candidatesInOrder: String*): BtlPreferences = {
    val preferencesPerCandidate = candidatesInOrder.zipWithIndex
      .map { case (candidate, index) => (candidate, (index + 1).toString) }

    btlPreferences(preferencesPerCandidate: _*)
  }

  def btlPreferences(prefPerCandidate: (String, String)*): BtlPreferences = {
    prefPerCandidate.map {
      case (posCode, rawPref) => candidatePosition(posCode) -> Preference.fromRawValue(rawPref).get
    }.toMap
  }

  private val candidatePositionCodePattern = "([A-Z]+)(\\d+)".r

  def candidatePosition(positionCode: String) = positionCode match {
    case candidatePositionCodePattern(groupCode, position) =>
      CandidatePosition(candidateFixture.groupLookup(groupCode), position.toInt)
  }

  def group(groupCode: String) = candidateFixture.groupLookup(groupCode) match {
    case g: Group => g
    case u: Ungrouped => throw new IllegalArgumentException(u.toString)
  }

  def candidateOrder(candidatesInOrder: String*): Vector[CandidatePosition] = {
    candidatesInOrder.map(candidatePosition).toVector
  }

  def groupOrder(groupsInOrder: String*): Vector[Group] = {
    groupsInOrder.map(group).toVector
  }
}