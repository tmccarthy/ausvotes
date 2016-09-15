package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.database.DriverComponent

case class BallotFactsRow(ballotId: String,
                          numAtlPreferences: Int,
                          numBtlPreferences: Int,
                          atlUsedSymbols: Boolean,
                          btlUsedSymbols: Boolean,
                          exhaustedAtCount: Option[Int],
                          candidatesElectedAtExhaustion: Option[Int],
                          isDonkeyVote: Boolean)

trait BallotFactsComponent { this: DriverComponent with BallotComponent with ComponentUtilities =>
  import driver.api._

  class BallotFactsTable(tag: Tag) extends Table[BallotFactsRow](tag, "BallotFacts") with CommonColumns {
    def ballotId = ballotIdColumn(O.PrimaryKey)

    def numAtlPreferences = column[Int]("numAtlPreferences")
    def numBtlPreferences = column[Int]("numBtlPreferences")

    def atlUsedSymbols = column[Boolean]("atlUsedSymbols")
    def btlUsedSymbols = column[Boolean]("btlUsedSymbols")

    def exhaustedAtCount = column[Option[Int]]("exhaustedAtCount")
    def candidatesElectedAtExhaustion = column[Option[Int]]("candidatesElectedAtExhaustion")

    def isDonkeyVote = column[Boolean]("isDonkeyVote")

    def * = (ballotId, numAtlPreferences, numBtlPreferences, atlUsedSymbols, btlUsedSymbols, exhaustedAtCount,
      candidatesElectedAtExhaustion, isDonkeyVote) <>
      (BallotFactsRow.tupled, BallotFactsRow.unapply)
  }

  val ballotFacts = TableQuery[BallotFactsTable]

  def ballotFactsFor(ballotId: String) = ballotFacts.filter(_.ballotId === ballotId)
}
