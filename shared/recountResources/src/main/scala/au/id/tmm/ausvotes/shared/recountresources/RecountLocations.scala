package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.shared.aws.data.S3ObjectKey
import au.id.tmm.utilities.hashing.StringHashing.StringHashingImplicits

object RecountLocations {

  def locationOfRecountFor(recountRequest: RecountRequest): S3ObjectKey = {
    val identifier = List(
      recountRequest.election.id,
      recountRequest.state.abbreviation,
      recountRequest.vacancies.toString,
      recountRequest.ineligibleCandidateAecIds.mkString(","),
    ).mkString("|")

    val hash = identifier.sha256checksum

    S3ObjectKey("recounts") / s"${hash.asHexString}.json"
  }

}
