package au.id.tmm.senatedb.data

import au.id.tmm.utilities.hashing.StringHashing.StringHashingImplicits

object BallotId {
  val length = "".sha256checksum.asBase64.length

  def computeFor(electionId: String,
                 stateCode: String,
                 voteCollectionPointId: Int,
                 batchNo: Int,
                 paperNo: Int): String = {
    s"$electionId|$stateCode|$voteCollectionPointId|$batchNo|$paperNo".sha256checksum.asBase64
  }
}
