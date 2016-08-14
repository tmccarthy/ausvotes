package au.id.tmm.senatedb.database

import au.id.tmm.utilities.hashing.StringHashing.StringHashingImplicits

object computeBallotId extends ((String,
  String, Int, Int, Int) => String) {
  override def apply(electionId: String,
                     stateCode: String,
                     voteCollectionPointId: Int,
                     batchNo: Int,
                     paperNo: Int): String = {
    s"$electionId|$stateCode|$voteCollectionPointId|$batchNo|$paperNo".sha256checksum.asBase64
  }
}
