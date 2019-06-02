package au.id.tmm.ausvotes.data_sources.nswec.parsed.impl

import au.id.tmm.ausvotes.data_sources.nswec.parsed.FetchLegCoBallots
import au.id.tmm.ausvotes.data_sources.nswec.raw.FetchRawLegCoPreferences
import au.id.tmm.ausvotes.model.nsw._
import au.id.tmm.ausvotes.model.nsw.legco._
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME.Ops
import cats.instances.string.catsKernelStdOrderForString

class FetchLegCoBallotsFromRaw[F[+_, +_] : BME : FetchRawLegCoPreferences] extends FetchLegCoBallots[F] {
  override def legCoBallotsFor(election: NswLegCoElection): F[Exception, fs2.Stream[F[Throwable, +?], Ballot]] = {

    FetchRawLegCoPreferences[F].legCoPreferencesFor(election).map { stream =>
      stream.groupAdjacentBy(row => row.ballotPaperID).map { case (rawBallotId, rowChunk) =>
        val rowsForBallot = rowChunk.toVector
        val headRow = rowsForBallot.head

        val district = District(election.stateElection, headRow.districtName)

        val vcpType: Option[NswVoteCollectionPoint.Special.Type] = headRow.venueName match {
          case "iVote"                   => Some(NswVoteCollectionPoint.Special.Type.IVote)
          case "Absent"                  => Some(NswVoteCollectionPoint.Special.Type.Absent)
          case "Postal"                  => Some(NswVoteCollectionPoint.Special.Type.Postal)
          case "Enrolment / Provisional" => Some(NswVoteCollectionPoint.Special.Type.EnrolmentOrProvisional)
          case "Declared Facility"       => Some(NswVoteCollectionPoint.Special.Type.DeclaredFacility)
        }

        val vcp = vcpType match {
          case Some(vcpType) => NswVoteCollectionPoint.Special(election.stateElection, district, vcpType)
          case None          => NswVoteCollectionPoint.PollingPlace(election.stateElection, district, headRow.venueName)
        }

        Ballot(
          election = election,
          jurisdiction = BallotJurisdiction(district, vcp),
          id = BallotId(???),
          groupPreferences = ???,
          candidatePreferences = ???,
        )
      }
    }

  }
}
