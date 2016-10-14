package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.fixtures.Candidates.CandidateFixture
import au.id.tmm.senatedb.model.parsing._

case class BallotMaker(candidateFixture: CandidateFixture) {

  def makeBallot(atlPreferences: Map[Group, Preference],
                 btlPreferences: Map[CandidatePosition, Preference],
                 division: Division,
                 pollingPlace: PollingPlace,
                 batch: Int = 1,
                 paper: Int = 1
            ) = {
    Ballot(candidateFixture.election, candidateFixture.state, division, pollingPlace, batch, paper, atlPreferences, btlPreferences)
  }

  def orderedAtlPreferences(groupsInOrder: String*): Map[Group, Preference] = {
    val preferencesPerGroup = groupsInOrder.zipWithIndex
      .map { case (group, index) => (group, (index + 1).toString) }

    atlPreferences(preferencesPerGroup: _*)
  }

  def atlPreferences(prefPerGroup: (String, String)*): Map[Group, Preference] = {
    prefPerGroup.map {
      case (groupCode, rawPref) => candidateFixture.groupLookup(groupCode) -> Preference(rawPref)
    }.map {
      case (group: Group, preference) => group -> preference
      case (Ungrouped, preference) => throw new IllegalArgumentException("Can't have Ungrouped above the line")
    }.toMap
  }

  def orderedBtlPreferences(candidatesInOrder: String*): Map[CandidatePosition, Preference] = {
    val preferencesPerCandidate = candidatesInOrder.zipWithIndex
      .map { case (candidate, index) => (candidate, (index + 1).toString) }

    btlPreferences(preferencesPerCandidate: _*)
  }

  def btlPreferences(prefPerCandidate: (String, String)*): Map[CandidatePosition, Preference] = {
    prefPerCandidate.map {
      case (posCode, rawPref) => codeToCandidatePosition(posCode) -> Preference(rawPref)
    }.toMap
  }

  private val candidatePositionCodePattern = "([A-Z]+)(\\d+)".r

  private def codeToCandidatePosition(positionCode: String) = positionCode match {
    case candidatePositionCodePattern(groupCode, position) =>
      CandidatePosition(candidateFixture.groupLookup(groupCode), position.toInt)
  }

  def candidateOrder(candidatesInOrder: String*): Vector[CandidatePosition] = {
    candidatesInOrder.map(codeToCandidatePosition).toVector
  }
}