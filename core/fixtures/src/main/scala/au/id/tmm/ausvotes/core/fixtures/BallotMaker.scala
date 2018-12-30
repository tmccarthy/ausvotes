package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.fixtures.CandidateFixture.CandidateFixture
import au.id.tmm.ausvotes.core.fixtures.GroupFixture.GroupFixture
import au.id.tmm.ausvotes.model.Preference
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.federal.{Division, FederalBallotJurisdiction, FederalPollingPlace}
import au.id.tmm.ausvotes.model.stv.BallotGroup

case class BallotMaker(candidateFixture: CandidateFixture) {

  def makeBallot(atlPreferences: AtlPreferences = Map.empty,
                 btlPreferences: BtlPreferences = Map.empty,
                 division: Division = DivisionFixture.ACT.CANBERRA,
                 pollingPlace: FederalPollingPlace = PollingPlaceFixture.ACT.BARTON,
                 batch: Int = 1,
                 paper: Int = 1
            ): SenateBallot = {
    SenateBallot(
      candidateFixture.election,
      FederalBallotJurisdiction(candidateFixture.state, division, pollingPlace),
      SenateBallotId(batch, paper),
      atlPreferences,
      btlPreferences,
    )
  }

  def orderedAtlPreferences(groupsInOrder: String*): AtlPreferences = {
    val preferencesPerGroup = groupsInOrder.zipWithIndex
      .map { case (group, index) => (group, (index + 1).toString) }

    atlPreferences(preferencesPerGroup: _*)
  }

  private def makePreference(rawCode: String): Preference = rawCode match {
    case "/" => Preference.Tick
    case "*" => Preference.Cross
    case number => Preference.Numbered(number.toInt)
  }

  def atlPreferences(prefPerGroup: (String, String)*): AtlPreferences = {
    prefPerGroup.map {
      case (groupCode, rawPref) => candidateFixture.groupLookup(groupCode) -> makePreference(rawPref)
    }.map {
      case (group: SenateGroup, preference) => group -> preference
      case (u: SenateUngrouped, preference) => throw new IllegalArgumentException("Can't have Ungrouped above the line")
    }.toMap
  }

  def orderedBtlPreferences(candidatesInOrder: String*): BtlPreferences = {
    val preferencesPerCandidate = candidatesInOrder.zipWithIndex
      .map { case (candidate, index) => (candidate, (index + 1).toString) }

    btlPreferences(preferencesPerCandidate: _*)
  }

  def btlPreferences(prefPerCandidate: (String, String)*): BtlPreferences = {
    prefPerCandidate.map {
      case (posCode, rawPref) => candidateFixture.candidateWithPosition(candidatePosition(posCode)) -> makePreference(rawPref)
    }.toMap
  }

  def candidatePosition(positionCode: String): SenateCandidatePosition =
    BallotMaker.candidatePosition(candidateFixture.groupFixture)(positionCode)

  def candidateWithPosition(positionCode: String): SenateCandidate =
    candidateFixture.candidateWithPosition(candidatePosition(positionCode))

  def group(groupCode: String): SenateGroup = candidateFixture.groupLookup(groupCode) match {
    case g: SenateGroup => g
    case u: SenateUngrouped => throw new IllegalArgumentException(u.toString)
  }

  def candidateOrder(candidatesInOrder: String*): Vector[SenateCandidatePosition] = {
    candidatesInOrder.map(candidatePosition).toVector
  }

  def groupOrder(groupsInOrder: String*): Vector[SenateGroup] = {
    groupsInOrder.map(group).toVector
  }
}

object BallotMaker {
  private val candidatePositionCodePattern = "([A-Z]+)(\\d+)".r

  def candidatePosition(groupFixture: GroupFixture)(positionCode: String): SenateCandidatePosition = positionCode match {
    case candidatePositionCodePattern(groupCode, position) =>
      SenateCandidatePosition(groupFixture.groupLookup(BallotGroup.Code(groupCode).right.get.asString), position.toInt)
  }
}
