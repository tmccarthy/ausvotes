package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.formalpreferences

import au.id.tmm.senatedb.data.database.model.BallotRow
import au.id.tmm.senatedb.data.{BallotId, BallotWithPreferences}
import au.id.tmm.senatedb.model.{SenateElection, State}

import scala.util.Try

object formalPreferencesCsvLineToEntities {

  def apply(election: SenateElection,
            state: State,
            rawPreferenceParser: RawPreferenceParser,
            ballotFactsCalculator: BallotFactsCalculator,
            row: Seq[String]): Try[BallotWithPreferences] = Try {

    val ballot = ballotRowOf(election, state, row)

    val preferenceString = row(5)
    val (atlPreferences, btlPreferences) = rawPreferenceParser.preferencesFrom(ballot.ballotId, preferenceString)
    val ballotFacts = ballotFactsCalculator.ballotFactsOf(ballot, atlPreferences, btlPreferences)

    BallotWithPreferences(ballot, ballotFacts, atlPreferences, btlPreferences)
  }

  private def ballotRowOf(election: SenateElection, state: State, row: Seq[String]): BallotRow = {
    val electorate = row(0)
    val voteCollectionPointId = row(2).toInt
    val batchNo = row(3).toInt
    val paperNo = row(4).toInt

    val ballotId = BallotId.computeFor(election.aecID, state.shortName, voteCollectionPointId, batchNo, paperNo)

    BallotRow(
      ballotId,
      election.aecID,
      state.shortName,
      electorate,
      voteCollectionPointId,
      batchNo,
      paperNo
    )
  }
}
