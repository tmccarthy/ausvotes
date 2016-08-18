package au.id.tmm.senatedb.data.entityconstruction

import au.id.tmm.senatedb.data.database.{CandidatesRow, GroupsRow}
import au.id.tmm.senatedb.model.SenateElection

import scala.util.Try

private[data] object firstPreferencesCsvLineToEntity extends ((SenateElection, Seq[String]) => Try[Either[GroupsRow, CandidatesRow]]) {

  def apply(election: SenateElection, csvLine: Seq[String]): Try[Either[GroupsRow, CandidatesRow]] = Try {
    val state = csvLine(0)
    val groupId = csvLine(1)
    val candidateId = csvLine(2)
    val positionInGroup = csvLine(3).toInt
    val name = csvLine(4)
    val party = csvLine(5)

    if (positionInGroup == 0) {
      Left(
        GroupsRow(
          groupId,
          election.aecID,
          state,
          party
        )
      )
    } else {
      Right(
        CandidatesRow(
          candidateId,
          election.aecID,
          state,
          groupId,
          positionInGroup - 1, // Make it zero-indexed
          name,
          party
        )
      )
    }
  }

}
