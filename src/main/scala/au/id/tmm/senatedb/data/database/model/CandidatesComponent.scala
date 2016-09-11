package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.database.DriverComponent
import au.id.tmm.senatedb.model.{CandidatePosition, SenateElection, State}
import au.id.tmm.utilities.string.StringUtils.ImprovedString

// TODO separate given and surnames
final case class CandidatesRow(candidateId: String,
                               election: String,
                               state: String,
                               group: String,
                               positionInGroup: Int,
                               name: String,
                               party: String) {
  lazy val position: CandidatePosition = CandidatePosition(group, positionInGroup)
}

object CandidatesRow {
  def tupled(tuple: (String, String, String, String, Int, String, String)): CandidatesRow = tuple match {
    case (candidateId, election, state, group, positionInGroup, name, party) =>
      CandidatesRow(candidateId.rtrim, election, state.rtrim, group.rtrim, positionInGroup, name.rtrim, party.rtrim)
  }
}

trait CandidatesComponent { this: DriverComponent with GroupsComponent with ComponentUtilities =>
  import driver.api._

  class CandidatesTable(tag: Tag) extends Table[CandidatesRow](tag, "Candidates") with CommonColumns {
    def candidateId = candidateIdColumn()

    def election = electionIdColumn()
    def state = stateColumn()

    def group = groupColumn()
    def positionInGroup = positionInGroupColumn()

    def name = nameColumn()
    def party = partyColumn()

    def pl = primaryKey("CANDIDATES_PK", (candidateId, election, state))

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
