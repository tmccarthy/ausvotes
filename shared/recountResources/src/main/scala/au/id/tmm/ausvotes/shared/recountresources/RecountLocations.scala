package au.id.tmm.ausvotes.shared.recountresources

import java.net.URLEncoder

import au.id.tmm.ausvotes.shared.aws.data.S3ObjectKey

object RecountLocations {

  def locationOfRecountFor(recountRequest: RecountRequest): S3ObjectKey = {

    val sanitisedCandidateIds = recountRequest.ineligibleCandidateAecIds.toList.sorted.map(sanitiseCandidateId)

    val ineligiblesPathSegment = sanitisedCandidateIds match {
      case ids @ _ :: _ => ids.mkString("-")
      case Nil => "none"
    }

    S3ObjectKey(
      "recounts",
      recountRequest.election.id,
      recountRequest.state.abbreviation,
      s"${recountRequest.vacancies.toString}-vacancies",
      s"$ineligiblesPathSegment-ineligible",
      "result.json",
    )
  }

  private def sanitiseCandidateId(candidateId: String): String =
    URLEncoder.encode(candidateId, "UTF-8").replace("-", "%2D")

}
