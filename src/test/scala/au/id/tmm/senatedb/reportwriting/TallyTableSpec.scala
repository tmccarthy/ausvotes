package au.id.tmm.senatedb.reportwriting

import au.id.tmm.senatedb.fixtures.{BallotMaker, Candidates, Divisions, PollingPlaces}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Party.{Independent, RegisteredParty}
import au.id.tmm.senatedb.model.parsing._
import au.id.tmm.senatedb.reportwriting.table.{Column, TallyTable}
import au.id.tmm.senatedb.tallies.{SimpleTally, Tally}
import au.id.tmm.utilities.collection.Matrix
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.geo.australia.State._
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class TallyTableSpec extends ImprovedFlatSpec {

  "the total formal ballots per state table" should "look as expected" in {
    val primaryCountTally = Tally(
      NSW -> 4492197d,
      VIC -> 3500237d,
      QLD -> 2723166d,
      WA -> 1366182d,
      SA -> 1061165d,
      TAS -> 339159d,
      ACT -> 254767d,
      NT -> 102027d
    )

    val totalCount = SimpleTally(13838900d)

    val columns = Vector(
      Column.StateNameColumn,
      Column.PrimaryCountColumn("Formal ballots in state"),
      Column.DenominatorCountColumn("Total formal ballots"),
      Column.FractionColumn("% of total")
    )

    val table = TallyTable[State](primaryCountTally, _ => totalCount.count, totalCount.count, totalCount.count, columns)

    val expected = Matrix(
      Vector("State", "Formal ballots in state", "Total formal ballots", "% of total"),
      Vector("NSW", "4,492,197", "13,838,900", "32.46%"),
      Vector("VIC", "3,500,237", "13,838,900", "25.29%"),
      Vector("QLD", "2,723,166", "13,838,900", "19.68%"),
      Vector("WA", "1,366,182", "13,838,900", "9.87%"),
      Vector("SA", "1,061,165", "13,838,900", "7.67%"),
      Vector("TAS", "339,159", "13,838,900", "2.45%"),
      Vector("ACT", "254,767", "13,838,900", "1.84%"),
      Vector("NT", "102,027", "13,838,900", "0.74%"),
      Vector("Total", "13,838,900", "13,838,900", "100.00%")
    )

    assert(table.asMatrix === expected)
  }

  "the total formal ballots nationally table" should "look as expected" in {
    val columns = Vector(
      Column.EmptyColumn,
      Column.PrimaryCountColumn("Formal ballots"),
      Column.DenominatorCountColumn("Total formal ballots"),
      Column.FractionColumn("% of total")
    )

    val table = TallyTable.totalRowOnly(13838900d, 13838900d, columns)

    val expected = Matrix(
      Vector("", "Formal ballots", "Total formal ballots", "% of total"),
      Vector("Total", "13,838,900", "13,838,900", "100.00%")
    )

    assert(table.asMatrix === expected)
  }

  it should "render in markdown correctly" in {
    val columns = Vector(
      Column.EmptyColumn,
      Column.PrimaryCountColumn("Formal ballots"),
      Column.DenominatorCountColumn("Total formal ballots"),
      Column.FractionColumn("% of total")
    )

    val table = TallyTable.totalRowOnly(13838900d, 13838900d, columns)

    val expectedMarkdown = "| |Formal ballots|Total formal ballots|% of total|\n" +
      "|---|---|---|---|\n" +
      "|**Total**|**13,838,900**|**13,838,900**|**100.00%**|"

    assert(table.asMarkdown === expectedMarkdown)
  }

  "a per first preferenced party table" should "look as expected" in {
    val primaryCountTally = Tally[Party](
      RegisteredParty("Apples") -> 5d,
      RegisteredParty("Oranges") -> 6d,
      Independent -> 2d
    )

    val denominatorTally = Tally[Party](
      RegisteredParty("Apples") -> 12d,
      RegisteredParty("Oranges") -> 10d,
      Independent -> 4d
    )

    val columns = Vector(
      Column.PartyNameColumn,
      Column.PrimaryCountColumn("Monkey votes"),
      Column.FractionColumn("% of total")
    )

    val table = TallyTable[Party](primaryCountTally, denominatorTally(_), 13, 26, columns)

    val expected = Matrix(
      Vector("Party", "Monkey votes", "% of total"),
      Vector("Oranges", "6", "60.00%"),
      Vector("Apples", "5", "41.67%"),
      Vector("Independent", "2", "50.00%"),
      Vector("Total", "13", "50.00%")
    )

    assert(table.asMatrix === expected)
  }

  "a per group table" should "look as expected" in {
    val ballotMaker = BallotMaker(Candidates.ACT)

    import ballotMaker.group

    val primaryCountTally = Tally[BallotGroup](
      group("A") -> 5d,
      Ungrouped(State.ACT) -> 2d
    )

    val denominatorTally = Tally[BallotGroup](
      group("A") -> 10d,
      Ungrouped(State.ACT) -> 8d
    )

    val columns = Vector(
      Column.StateNameColumn,
      Column.GroupNameColumn,
      Column.PartyNameColumn,
      Column.PrimaryCountColumn("Monkey votes"),
      Column.FractionColumn("% of total")
    )

    val table = TallyTable[BallotGroup](primaryCountTally, denominatorTally(_), 13, 26, columns)

    val expected = Matrix(
      Vector("State", "Group", "Party", "Monkey votes", "% of total"),
      Vector("ACT", "A (Liberal Democratic Party)", "Liberal Democratic Party", "5", "50.00%"),
      Vector("ACT", "UG (Ungrouped)", "Independent", "2", "25.00%"),
      Vector("Total", "", "", "13", "50.00%")
    )

    assert(table.asMatrix === expected)
  }

  "a per division table" should "look as expected" in {
    val primaryCountTally = Tally[Division](
      Divisions.ACT.CANBERRA -> 5d,
      Divisions.NT.LINGIARI -> 2d
    )

    val denominatorTally = Tally[Division](
      Divisions.ACT.CANBERRA -> 10d,
      Divisions.NT.LINGIARI -> 8d
    )

    val columns = Vector(
      Column.StateNameColumn,
      Column.DivisionNameColumn,
      Column.PrimaryCountColumn("Monkey votes"),
      Column.FractionColumn("% of total")
    )

    val table = TallyTable[Division](primaryCountTally, denominatorTally(_), 13, 26, columns)

    val expected = Matrix(
      Vector("State", "Division", "Monkey votes", "% of total"),
      Vector("ACT", "Canberra", "5", "50.00%"),
      Vector("NT", "Lingiari", "2", "25.00%"),
      Vector("Total", "", "13", "50.00%")
    )

    assert(table.asMatrix === expected)
  }

  "a per vote vote collection place table" should "look as expected" in {
    val absenteeVoteCollectionPoint =
      VoteCollectionPoint.Absentee(SenateElection.`2016`, State.NT, Divisions.NT.SOLOMON, 1)

    val primaryCountTally = Tally[VoteCollectionPoint](
      PollingPlaces.ACT.BARTON -> 5d,
      absenteeVoteCollectionPoint -> 2d
    )

    val denominatorTally = Tally[VoteCollectionPoint](
      PollingPlaces.ACT.BARTON -> 10d,
      absenteeVoteCollectionPoint -> 8d
    )

    val columns = Vector(
      Column.StateNameColumn,
      Column.DivisionNameColumn,
      Column.VoteCollectionPointNameColumn,
      Column.PrimaryCountColumn("Monkey votes"),
      Column.FractionColumn("% of total")
    )

    val table = TallyTable[VoteCollectionPoint](primaryCountTally, denominatorTally(_), 13, 26, columns)

    val expected = Matrix(
      Vector("State", "Division", "Vote collection point", "Monkey votes", "% of total"),
      Vector("ACT", "Canberra", "Barton", "5", "50.00%"),
      Vector("NT", "Solomon", "ABSENTEE 1", "2", "25.00%"),
      Vector("Total", "", "", "13", "50.00%")
    )

    assert(table.asMatrix === expected)
  }

  "a table" should "produce markdown as expected" in {
    val totalCount = SimpleTally(13838900d)
    val columns = Vector(
      Column.StateNameColumn,
      Column.PrimaryCountColumn("Formal ballots"),
      Column.DenominatorCountColumn("Total formal ballots"),
      Column.FractionColumn("% of total")
    )

    val table = TallyTable[Nothing](Tally(), _ => fail(), totalCount.count, totalCount.count, columns)

    val expectedMarkdown = "|State|Formal ballots|Total formal ballots|% of total|\n" +
      "|---|---|---|---|\n" +
      "|**Total**|**13,838,900**|**13,838,900**|**100.00%**|"

    assert(table.asMarkdown === expectedMarkdown)
  }
}
