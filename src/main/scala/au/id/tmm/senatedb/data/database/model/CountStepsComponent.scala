package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.database.DriverComponent
import au.id.tmm.senatedb.data.database.model.CountStepRow.StepType
import au.id.tmm.senatedb.data.database.model.CountStepRow.StepType.StepType
import au.id.tmm.utilities.string.StringUtils.ImprovedString

final case class CountStepRow(election: String,
                              state: String,
                              count: Int,

                              transferValue: Double,

                              exhaustedPapers: Int,
                              exhaustedVotesTransferred: Int,
                              exhaustedProgressiveVoteTotal: Int,

                              gainLossPapers: Int,
                              gainLossVoteTransferred: Int,
                              gainLossProgressiveVoteTotal: Int,

                              stepType: StepType,
                              votesDistributedFromGroup: Option[String],
                              votesDistributedFromPositionInGroup: Option[Int],
                              progressiveNumCandidatesElected: Int)

object CountStepRow {

  def tupled(tuple: (String, String, Int, Double, Int, Int, Int, Int, Int, Int, StepType, Option[String], Option[Int], Int)): CountStepRow = {
    tuple match {
      case (election, state, count, transferValue, exhaustedPapers, exhaustedVotesTransferred,
      exhaustedProgressiveVoteTotal, gainLossPapers, gainLossVoteTransferred, gainLossProgressiveVoteTotal, stepType,
      votesDistributedFromGroup, votesDistributedFromPositionInGroup, progressiveNumCandidatesElected) => CountStepRow(
        election.rtrim,
        state.rtrim,
        count,
        transferValue,
        exhaustedPapers,
        exhaustedVotesTransferred,
        exhaustedProgressiveVoteTotal,
        gainLossPapers,
        gainLossVoteTransferred,
        gainLossProgressiveVoteTotal,
        stepType,
        votesDistributedFromGroup.map(_.rtrim),
        votesDistributedFromPositionInGroup,
        progressiveNumCandidatesElected
      )
    }
  }

  object StepType extends Enumeration {
    type StepType = Value

    val INITIAL = Value("i")
    val DISTRIBUTED_FROM_ELECTED = Value("e")
    val DISTRIBUTED_FROM_EXCLUDED = Value("x")
  }
}

trait CountStepsComponent { this: DriverComponent with ComponentUtilities =>

  import driver.api._

  implicit val stepTypeMapper = MappedColumnType.base[StepType, Char](
    stepType => stepType.toString.charAt(0),
    char => StepType.withName(char.toString)
  )

  class CountStepsTable(tag: Tag) extends Table[CountStepRow](tag, "CountSteps") with CommonColumns {
    def election = electionIdColumn()
    def state = stateColumn()
    def count = countColumn()

    def transferValue = column[Double]("transferValue")

    def exhaustedPapers = column[Int]("exhaustedPapers")
    def exhaustedVotesTransferred = column[Int]("exhaustedVotesTransferred")
    def exhaustedProgressiveVoteTotal = column[Int]("exhaustedProgressiveVoteTotal")

    def gainLossPapers = column[Int]("gainLossPapers")
    def gainLossVoteTransferred = column[Int]("gainLossVoteTransferred")
    def gainLossProgressiveVoteTotal = column[Int]("gainLossProgressiveVoteTotal")

    def stepType = column[StepType]("stepType")
    def votesDistributedFromGroup = column[Option[String]](groupColumnName, groupLength)
    def votesDistributedFromPositionInGroup = column[Option[Int]](positionInGroupColumnName)
    def progressiveNumCandidatesElected = column[Int]("progressiveNumCandidatesElected")

    def pk = primaryKey("DOP_STEP_PK", (election, state, count))

    def * = (election,
      state,
      count,
      transferValue,
      exhaustedPapers,
      exhaustedVotesTransferred,
      exhaustedProgressiveVoteTotal,
      gainLossPapers,
      gainLossVoteTransferred,
      gainLossProgressiveVoteTotal,
      stepType,
      votesDistributedFromGroup,
      votesDistributedFromPositionInGroup,
      progressiveNumCandidatesElected) <> (CountStepRow.tupled, CountStepRow.unapply)
  }

  val countSteps = TableQuery[CountStepsTable]

  def countStepsFor(election: String, state: String) = countSteps
    .filter(_.election === election)
    .filter(_.state === state)

  def insertCountStep(countStepRow: CountStepRow) = countSteps += countStepRow
}
