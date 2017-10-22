package au.id.tmm.ausvotes.core.model.computation

sealed trait BallotExhaustion {

}

object BallotExhaustion {

  /**
    * This is the case where all preferenced candidates were ineligible and hence not included in the count
    */
  final case object ExhaustedBeforeInitialAllocation extends BallotExhaustion

  final case class Exhausted(atCount: Int,
                             value: Double,
                             candidatesElectedAtExhaustion: Int) extends BallotExhaustion

  case object NotExhausted extends BallotExhaustion
}