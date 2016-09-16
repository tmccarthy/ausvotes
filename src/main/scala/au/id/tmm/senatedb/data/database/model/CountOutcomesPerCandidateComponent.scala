package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.database.DriverComponent
import au.id.tmm.senatedb.data.database.model.CandidateOutcome.CandidateOutcome

final case class CountOutcomesPerCandidateRow(election: String,
                                              state: String,
                                              candidateId: String,

                                              outcome: CandidateOutcome,
                                              outcomeOrder: Int,
                                              outcomeAtCount: Int)

object CandidateOutcome extends Enumeration {
  type CandidateOutcome = Value

  val ELECTED = Value("e")
  val EXCLUDED = Value("x")
}

trait CountOutcomesPerCandidateComponent { this: DriverComponent with ComponentUtilities =>

  import driver.api._

  implicit val candidateOutcomeMapper = MappedColumnType.base[CandidateOutcome, Char](
    status => status.toString.charAt(0),
    char => CandidateOutcome.withName(char.toString)
  )

  class CountOutcomesPerCandidateTable(tag: Tag)
    extends Table[CountOutcomesPerCandidateRow](tag, "CandidateOutcomesPerCandidate")
      with CommonColumns {

    def election = electionIdColumn()
    def state = stateColumn()
    def candidate = candidateIdColumn()

    def outcome = column[CandidateOutcome]("outcome")
    def outcomeOrder = column[Int]("outcomeOrder")
    def outcomeAtCount = column[Int]("outcomeAtCount")

    def pk = primaryKey("COUNT_OUTCOMES_PK", (election, state, candidate))

    def * = (election, state, candidate, outcome, outcomeOrder, outcomeAtCount) <>
      (CountOutcomesPerCandidateRow.tupled, CountOutcomesPerCandidateRow.unapply)
  }

  val outcomesPerCandidate = TableQuery[CountOutcomesPerCandidateTable]

  def candidateOutcomesFor(electionId: String, state: String) =
    outcomesPerCandidate
    .filter(_.election === electionId)
    .filter(_.state === state)

  def outcomeForCandidate(electionId: String, state: String, candidateId: String) = candidateOutcomesFor(electionId, state)
      .filter(_.candidate === candidateId)
}
