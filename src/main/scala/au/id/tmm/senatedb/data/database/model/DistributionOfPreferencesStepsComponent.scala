package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.database.DriverComponent
import au.id.tmm.senatedb.data.database.model.DistributionOfPreferencesStepRow.StepType
import au.id.tmm.senatedb.data.database.model.DistributionOfPreferencesStepRow.StepType.StepType
import au.id.tmm.utilities.string.StringUtils.ImprovedString

final case class DistributionOfPreferencesStepRow(election: String,
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
                                                  stepTargetGroup: Option[String],
                                                  stepTargetPositionInGroup: Option[Int])

object DistributionOfPreferencesStepRow {

  def tupled(tuple: (String, String, Int, Double, Int, Int, Int, Int, Int, Int, StepType, Option[String], Option[Int])): DistributionOfPreferencesStepRow = {
    tuple match {
      case (election, state, count, transferValue, exhaustedPapers, exhaustedVotesTransferred,
      exhaustedProgressiveVoteTotal, gainLossPapers, gainLossVoteTransferred, gainLossProgressiveVoteTotal, stepType,
      stepTargetGroup, stepTargetPositionInGroup) => DistributionOfPreferencesStepRow(
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
        stepTargetGroup.map(_.rtrim),
        stepTargetPositionInGroup
      )
    }
  }

  object StepType extends Enumeration {
    type StepType = Value

    val INITIAL = Value("i")
    val ELECT = Value("e")
    val EXCLUDE = Value("x")
  }
}

trait DistributionOfPreferencesStepsComponent { this: DriverComponent with ComponentUtilities =>

  import driver.api._

  implicit val stepTypeMapper = MappedColumnType.base[StepType, Char](
    stepType => stepType.toString.charAt(0),
    char => StepType.withName(char.toString)
  )

  class DistributionOfPreferencesStepsTable(tag: Tag) extends Table[DistributionOfPreferencesStepRow](tag, "DistributionOfPreferencesSteps") with CommonColumns {
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
    def stepTargetGroup = column[Option[String]](groupColumnName, groupLength)
    def stepTargetPositionInGroup = column[Option[Int]](positionInGroupColumnName)

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
      stepTargetGroup,
      stepTargetPositionInGroup) <> (DistributionOfPreferencesStepRow.tupled, DistributionOfPreferencesStepRow.unapply)
  }

}
