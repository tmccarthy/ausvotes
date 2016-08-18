package au.id.tmm.senatedb.data.database

import au.id.tmm.senatedb.model.{SenateElection, State}

final case class CandidatesRow(candidateId: String,
                               election: String,
                               state: String,
                               group: String,
                               positionInGroup: Int,
                               name: String,
                               party: String)

trait CandidatesComponent { this: DriverComponent with GroupsComponent =>
  import driver.api._

  class CandidatesTable(tag: Tag) extends Table[CandidatesRow](tag, "Candidates") {
    def candidateId = column[String]("candidateId", O.PrimaryKey)

    def election = column[String]("electionId")
    def state = column[String]("state")

    def group = column[String]("group")
    def positionInGroup = column[Int]("positionInGroup")

    def name = column[String]("name")
    def party = column[String]("party")

    def * = (candidateId, election, state, group, positionInGroup, name, party) <> (CandidatesRow.tupled, CandidatesRow.unapply)

  }

  val candidates: TableQuery[CandidatesTable] = TableQuery[CandidatesTable]

  def candidatesForElection(election: SenateElection) = candidates
    .filter(candidate => candidate.election === election.aecID)

  def candidatesForElectionInState(election: SenateElection, state: State) = candidatesForElection(election)
    .filter(candidate => candidate.state === state.shortName)

  def insertCandidates(toInsert: Iterable[CandidatesRow]) = candidates ++= toInsert
}
