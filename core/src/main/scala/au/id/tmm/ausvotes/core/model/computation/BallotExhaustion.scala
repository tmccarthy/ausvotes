package au.id.tmm.ausvotes.core.model.computation

sealed trait BallotExhaustion {

}

object BallotExhaustion {
  final case class Exhausted(atCount: Int,
                             value: Double,
                             candidatesElectedAtExhaustion: Int) extends BallotExhaustion

  case object NotExhausted extends BallotExhaustion
}