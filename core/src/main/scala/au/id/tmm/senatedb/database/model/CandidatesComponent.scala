package au.id.tmm.senatedb.database.model

final case class CandidatesRow(candidateId: String,
                               election: String,
                               state: String,
                               group: String,
                               candidatePosition: Int,
                               name: String,
                               party: String)

trait CandidatesComponent { this: DriverComponent =>
  import driver.api._

  class CandidatesTable(tag: Tag) extends Table[CandidatesRow](tag, "Candidates") {
    def candidateId = column[String]("candidateId", O.PrimaryKey)

    def election = column[String]("electionId")
    def state = column[String]("state")

    def group = column[String]("group")
    def candidatePosition = column[Int]("candidatePosition")

    def name = column[String]("name")
    def party = column[String]("party")

    def * = (candidateId, election, state, group, candidatePosition, name, party) <> (CandidatesRow.tupled, CandidatesRow.unapply)

  }

  def candidates = TableQuery[CandidatesTable]

}
