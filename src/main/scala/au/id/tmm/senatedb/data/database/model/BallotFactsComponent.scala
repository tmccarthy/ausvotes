package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.BallotId
import au.id.tmm.senatedb.data.database.DriverComponent

case class BallotFactsRow(ballotId: String,
                          numAtlPreferences: Int,
                          numBtlPreferences: Int,
                          atlUsedSymbols: Boolean,
                          btlUsedSymbols: Boolean)

trait BallotFactsComponent { this: DriverComponent with BallotComponent =>
  import driver.api._

  class BallotFactsTable(tag: Tag) extends Table[BallotFactsRow](tag, "BallotFacts") {
    def ballotId = column[String]("ballotId", O.PrimaryKey, O.Length(BallotId.length, varying = false))

    def numAtlPreferences = column[Int]("numAtlPreferences")
    def numBtlPreferences = column[Int]("numBtlPreferences")
    def atlUsedSymbols = column[Boolean]("atlUsedSymbols")
    def btlUsedSymbols = column[Boolean]("btlUsedSymbols")

    def * = (ballotId, numAtlPreferences, numBtlPreferences, atlUsedSymbols, btlUsedSymbols) <>
      (BallotFactsRow.tupled, BallotFactsRow.unapply)
  }

  val ballotFacts = TableQuery[BallotFactsTable]

  def ballotFactsFor(ballotId: String) = ballotFacts.filter(_.ballotId === ballotId)
}
