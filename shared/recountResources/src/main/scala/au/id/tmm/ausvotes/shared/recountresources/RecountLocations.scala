package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.shared.aws.data.S3ObjectKey

object RecountLocations {

  def locationOfRecountFor(recountRequest: RecountRequest): S3ObjectKey = {

    val sanitisedCandidateIds = recountRequest.ineligibleCandidateAecIds.toList.map(_.asLong).sorted

    val ineligiblesPathSegment = sanitisedCandidateIds match {
      case ids @ _ :: _ => ids.mkString("-")
      case Nil => "none"
    }

    S3ObjectKey(
      "recounts",
      recountRequest.election.election.id.asString,
      recountRequest.election.state.abbreviation,
      s"${recountRequest.vacancies.toString}-vacancies",
      s"$ineligiblesPathSegment-ineligible",
      if (recountRequest.doRounding) "with-rounding" else "no-rounding",
      "result.json",
    )
  }

}
