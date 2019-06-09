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

        for {
          _ <- specialVcpTypeFrom(headRow.venueName)
          _ <- pollingPlaceFrom(headRow.voteTypeName)
        } yield ()

        ???
      }
    }
  }

  private def specialVcpTypeFrom(code: String): Either[Unit, NswVoteCollectionPoint.Special.Type] = code match {
    case "iVote"                   => Right(NswVoteCollectionPoint.Special.Type.IVote)
    case "Absent"                  => Right(NswVoteCollectionPoint.Special.Type.Absent)
    case "Postal"                  => Right(NswVoteCollectionPoint.Special.Type.Postal)
    case "Enrolment / Provisional" => Right(NswVoteCollectionPoint.Special.Type.EnrolmentOrProvisional)
    case "Declared Facility"       => Right(NswVoteCollectionPoint.Special.Type.DeclaredFacility)
    case _                         => Left(())
  }

  private def pollingPlaceFrom(code: String): Either[Unit, NswVoteCollectionPoint.PollingPlace.Type] = code match {
    case "PP" => Right(NswVoteCollectionPoint.PollingPlace.Type.VotingCentre)
    case "PR" => Right(NswVoteCollectionPoint.PollingPlace.Type.EarlyVotingCentre)
    case "DV" => Right(NswVoteCollectionPoint.PollingPlace.Type.DeclarationVote)
    case "DI" => ???
    case _    => Left(())
  }
}
