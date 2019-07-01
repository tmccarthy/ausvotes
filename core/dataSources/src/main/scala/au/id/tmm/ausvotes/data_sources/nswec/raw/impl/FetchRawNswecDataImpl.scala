package au.id.tmm.ausvotes.data_sources.nswec.raw.impl

import au.id.tmm.ausvotes.data_sources.common.CsvParsing._
import au.id.tmm.ausvotes.data_sources.common.streaming.{MakeSource, ReadingInputStreams}
import au.id.tmm.ausvotes.data_sources.nswec.raw.{FetchRawLegCoGroupsAndCandidates, FetchRawLegCoPreferences}
import au.id.tmm.ausvotes.data_sources.nswec.resources.{LegCoGroupsAndCandidatesResource, LegCoPreferencesResource}
import au.id.tmm.ausvotes.model.nsw.legco._
import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync.Ops
import com.github.tototoshi.csv

final class FetchRawNswecDataImpl[F[+_, +_] : Sync] private (
  makeSourceForLegCoPreferencesResource: MakeSource[F, Exception, LegCoPreferencesResource],
) extends FetchRawLegCoPreferences[F]
  with FetchRawLegCoGroupsAndCandidates[F] {

  private val csvFormat = csv.defaultCSVFormat

  override def legCoPreferencesFor(election: NswLegCoElection): F[FetchRawLegCoPreferences.Error, fs2.Stream[F[Throwable, +?], FetchRawLegCoPreferences.Row]] =
    for {
      lines <- makeSourceForLegCoPreferencesResource(LegCoPreferencesResource(election))
        .leftMap(FetchRawLegCoPreferences.Error)
      csvRows = ReadingInputStreams.streamCsv(lines, csvFormat)
      rows <- Sync.pureCatchException {
        csvRows.drop(2)
          .map { row =>
            FetchRawLegCoPreferences.Row(
              row(0).toInt,
              row(1),
              row(2),
              row(3),
              row(4),
              parsePossibleString(row(5)),
              parsePossibleInt(row(6)),
              noneIfBlank(row(7)),
              noneIfBlank(row(8)),
              parsePossibleInt(row(9)),
              row(10) == "Formal",
              parsePossibleString(row(11)),
            )
          }
      }.leftMap(FetchRawLegCoPreferences.Error)
    } yield rows

  override def groupAndCandidateRowsFor(
    election: NswLegCoElection,
  ): F[Exception, GroupsAndCandidates] = ???
}

object FetchRawNswecDataImpl {
  def apply[F[+_, +_] : Sync](
    makeSourceForLegCoPreferencesResource: MakeSource[F, Exception, LegCoPreferencesResource],
    makeSourceForLegCoGroupsAndCandidatesResource: MakeSource[F, Exception, LegCoGroupsAndCandidatesResource],
  ) = new FetchRawNswecDataImpl[F](
    makeSourceForLegCoPreferencesResource,
    makeSourceForLegCoGroupsAndCandidatesResource,
  )
}
