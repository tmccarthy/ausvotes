package au.id.tmm.ausvotes.data_sources.nswec.parsed.impl

import au.id.tmm.ausvotes.data_sources.nswec.parsed.FetchLegCoBallots
import au.id.tmm.ausvotes.data_sources.nswec.raw.FetchRawLegCoPreferences
import au.id.tmm.ausvotes.model.nsw._
import au.id.tmm.ausvotes.model.nsw.legco._
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME.Ops

class FetchLegCoBallotsFromRaw[F[+_, +_] : BME : FetchRawLegCoPreferences] extends FetchLegCoBallots[F] {
  override def legCoBallotsFor(election: NswLegCoElection): F[Exception, fs2.Stream[F[Throwable, +?], Ballot]] = {

    FetchRawLegCoPreferences[F].legCoPreferencesFor(election).map { stream =>
      stream.map { row =>
        val district = District(election.stateElection, row.districtName, ???)

        // TODO really need to separate the "special vcp type" notion out
        val voteCollectionPoint: Vcp = row.venueName match {
          case "iVote" => ???
          case "Absent" => ???
          case "Postal" => ???
          case "Enrolment / Provisional" => ???
          case "Declared Facility" => ???
        }

        Ballot(
          election = election,
          jurisdiction = BallotJurisdiction(district, voteCollectionPoint),
          id = BallotId(row.ballotPaperID),
          groupPreferences = ???,
          candidatePreferences = ???,
        )
      }
    }

  }
}
