package au.id.tmm.ausvotes.core.reportwriting

import java.nio.file.Files

import au.id.tmm.ausvotes.core.fixtures.DivisionFixture
import au.id.tmm.ausvotes.core.model.parsing.Division
import au.id.tmm.ausvotes.core.reportwriting.table.{Column, TallyTable}
import au.id.tmm.ausvotes.core.tallies.{Tally0, Tally1}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.geo.australia.State._
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class ReportWriterSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  "the report writer" should "write the report markdown to a file" in {
    Given("a report with two tables")

    val table1 = {
      val primaryCountTally = Tally1[State](
        NSW -> 4492197d,
        VIC -> 3500237d,
        QLD -> 2723166d,
        WA -> 1366182d,
        SA -> 1061165d,
        TAS -> 339159d,
        ACT -> 254767d,
        NT -> 102027d
      )

      val totalCount = Tally0(13838900d)

      val columns = Vector(
        Column.StateNameColumn,
        Column.PrimaryCountColumn("Formal ballots in state"),
        Column.DenominatorCountColumn("Total formal ballots"),
        Column.FractionColumn("% of total")
      )

      TallyTable[State](primaryCountTally, _ => totalCount.value, totalCount.value, totalCount.value, columns)
        .withTitle("Total formal ballots")
    }

    val table2 = {
      val primaryCountTally = Tally1[Division](
        DivisionFixture.ACT.CANBERRA -> 5d,
        DivisionFixture.NT.LINGIARI -> 2d
      )

      val denominatorTally = Tally1[Division](
        DivisionFixture.ACT.CANBERRA -> 10d,
        DivisionFixture.NT.LINGIARI -> 8d
      )

      val columns = Vector(
        Column.StateNameColumn,
        Column.DivisionNameColumn,
        Column.PrimaryCountColumn("Monkey votes"),
        Column.FractionColumn("% of total")
      )

      TallyTable[Division](primaryCountTally, denominatorTally(_).value, 13, 26, columns)
        .withTitle("Monkey votes by division")
    }

    val report = Report("Test report!", Vector(table1, table2))

    When("the report is written")
    ReportWriter.writeReport(cleanDirectory, report)

    Then("the markdown is correct")
    val expectedLocation = cleanDirectory resolve "Test_report.md"

    val actualContent = new String(Files.readAllBytes(expectedLocation), "UTF-8")

    val expectedContent = """# Test report!
                            |
                            |### Total formal ballots
                            |
                            ||State|Formal ballots in state|Total formal ballots|% of total|
                            ||---|---|---|---|
                            ||NSW|4,492,197|13,838,900|32.46%|
                            ||VIC|3,500,237|13,838,900|25.29%|
                            ||QLD|2,723,166|13,838,900|19.68%|
                            ||WA|1,366,182|13,838,900|9.87%|
                            ||SA|1,061,165|13,838,900|7.67%|
                            ||TAS|339,159|13,838,900|2.45%|
                            ||ACT|254,767|13,838,900|1.84%|
                            ||NT|102,027|13,838,900|0.74%|
                            ||**Total**|**13,838,900**|**13,838,900**|**100.00%**|
                            |
                            |### Monkey votes by division
                            |
                            ||State|Division|Monkey votes|% of total|
                            ||---|---|---|---|
                            ||ACT|Canberra|5|50.00%|
                            ||NT|Lingiari|2|25.00%|
                            ||**Total**|****|**13**|**50.00%**|
                            |""".stripMargin

    assert(expectedContent === actualContent)
  }

}
