package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.database.DriverComponent
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.string.StringUtils.ImprovedString

final case class CandidatesRow(candidateId: String,
                               election: String,
                               state: String,
                               group: String,
                               positionInGroup: Int,
                               name: String,
                               party: String)

object CandidatesRow {
  def tupled(tuple: (String, String, String, String, Int, String, String)): CandidatesRow = tuple match {
    case (candidateId, election, state, group, positionInGroup, name, party) =>
      CandidatesRow(candidateId.rtrim, election, state.rtrim, group.rtrim, positionInGroup, name.rtrim, party.rtrim)
  }
}

trait CandidatesComponent { this: DriverComponent with GroupsComponent with ComponentUtilities =>
  import driver.api._

  class CandidatesTable(tag: Tag) extends Table[CandidatesRow](tag, "Candidates") with CommonColumns {
    def candidateId = candidateIdColumn(O.PrimaryKey)

    def election = electionIdColumn()
    def state = stateColumn()

    def group = groupColumn()
    def positionInGroup = positionInGroupColumn()

    def name = nameColumn()
    def party = partyColumn()

    def * = (candidateId, election, state, group, positionInGroup, name, party) <>
      (CandidatesRow.tupled, CandidatesRow.unapply)
  }

  val candidates: TableQuery[CandidatesTable] = TableQuery[CandidatesTable]

  def candidatesForElection(election: SenateElection) = candidates
    .filter(candidate => candidate.election === election.aecID)

  def candidatesForElectionInState(election: SenateElection, state: State) = candidatesForElection(election)
    .filter(candidate => candidate.state === state.shortName)

  def insertCandidates(toInsert: Iterable[CandidatesRow]) = candidates ++= toInsert
}
