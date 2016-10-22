package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.parsing.{Division, Party, VoteCollectionPoint}
import au.id.tmm.utilities.geo.australia.State

trait TallyReport { this: Report[_] =>
  def total: Long
  def perState: Map[State, Long]
  def perDivision: Map[Division, Long]
  def perVoteCollectionPlace: Map[VoteCollectionPoint, Long]
  def perFirstPreferencedParty: Map[Option[Party], Long]
}
