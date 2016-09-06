package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.CountData.CountStepData
import au.id.tmm.senatedb.data.database.model.{CountOutcomesPerCandidateRow, CountStepRow, CountTransfersPerCandidateRow}
import au.id.tmm.senatedb.model.{SenateElection, State}

final case class CountData(election: SenateElection,
                           state: State,

                           steps: List[CountStepData],
                           outcomes: Set[CountOutcomesPerCandidateRow])

object CountData {
  final case class CountStepData(stepRow: CountStepRow, transfers: Set[CountTransfersPerCandidateRow])
}