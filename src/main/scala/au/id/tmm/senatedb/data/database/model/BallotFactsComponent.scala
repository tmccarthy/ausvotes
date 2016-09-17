package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.database.DriverComponent

case class BallotFactsRow(ballotId: String,
                          numCellsNumberedAtl: Int,
                          numCellsNumberedBtl: Int,
                          numFormalPreferencesAtl: Int,
                          numFormalPreferencesBtl: Int,
                          atlUsedSymbols: Boolean,
                          btlUsedSymbols: Boolean,
                          exhaustedAtCount: Option[Int],
                          candidatesElectedAtExhaustion: Option[Int],
                          isDonkeyVote: Boolean)

trait BallotFactsComponent { this: DriverComponent with BallotComponent with ComponentUtilities =>
  import driver.api._

  class BallotFactsTable(tag: Tag) extends Table[BallotFactsRow](tag, "BallotFacts") with CommonColumns {
    def ballotId = ballotIdColumn(O.PrimaryKey)

    def numCellsNumberedAtl = column[Int]("numCellsNumberedAtl")
    def numCellsNumberedBtl = column[Int]("numCellsNumberedBtl")

    def numFormalPreferencesAtl = column[Int]("numFormalPreferencesAtl")
    def numFormalPreferencesBtl = column[Int]("numFormalPreferencesBtl")

    def atlUsedSymbols = column[Boolean]("atlUsedSymbols")
    def btlUsedSymbols = column[Boolean]("btlUsedSymbols")

    def exhaustedAtCount = column[Option[Int]]("exhaustedAtCount")
    def candidatesElectedAtExhaustion = column[Option[Int]]("candidatesElectedAtExhaustion")

    def isDonkeyVote = column[Boolean]("isDonkeyVote")

    def * = (ballotId, numCellsNumberedAtl, numCellsNumberedBtl, numFormalPreferencesAtl, numFormalPreferencesBtl,
      atlUsedSymbols, btlUsedSymbols, exhaustedAtCount, candidatesElectedAtExhaustion, isDonkeyVote) <>
      (BallotFactsRow.tupled, BallotFactsRow.unapply)
  }

  val ballotFacts = TableQuery[BallotFactsTable]

  def ballotFactsFor(ballotId: String) = ballotFacts.filter(_.ballotId === ballotId)
}
