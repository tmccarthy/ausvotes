package au.id.tmm.senatedb.data.entityconstruction

import au.id.tmm.senatedb.data.database.{CandidatesRow, GroupsRow}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class firstPreferencesCsvLineToEntitySpec extends ImprovedFlatSpec {

  private val groupedCandidateLine = List("ACT","G","28254","1","FIELD, Deborah","Animal Justice Party","509","8","11","22","33","583")
  private val groupLine = List("ACT","H","29665","0","H Ticket Votes","The Greens","26508","479","199","2038","1424","30648")
  private val testElection = SenateElection.`2016`

  behaviour of "the firstPreferencesCsvLineToEntity method for a candidate line"

  it should "return a candidate" in {
    val groupOrCandidate = firstPreferencesCsvLineToEntity(testElection, groupedCandidateLine).get

    assert(groupOrCandidate.isRight)
  }

  it should "return a candidate with the correct data" in {
    val candidate = firstPreferencesCsvLineToEntity(testElection, groupedCandidateLine).get.right.get

    assert(candidate === CandidatesRow("28254", testElection.aecID, "ACT", "G", 0, "FIELD, Deborah", "Animal Justice Party"))
  }

  behaviour of "the firstPreferencesCsvLineToEntity method for a group line"

  it should "return a group" in {
    val groupOrCandidate = firstPreferencesCsvLineToEntity(testElection, groupLine).get

    assert(groupOrCandidate.isLeft)
  }

  it should "return a group with the correct data" in {
    val group = firstPreferencesCsvLineToEntity(testElection, groupLine).get.left.get

    assert(group === GroupsRow("H", testElection.aecID, "ACT", "The Greens"))
  }
}
