package au.id.tmm.ausvotes.core.model.computation

import au.id.tmm.countstv.model.values.{Count, TransferValue}

sealed trait BallotExhaustion {

}

object BallotExhaustion {

  final case class Exhausted(atCount: Count,
                             value: TransferValue,
                             candidatesElectedAtExhaustion: Int) extends BallotExhaustion

  case object NotExhausted extends BallotExhaustion
}