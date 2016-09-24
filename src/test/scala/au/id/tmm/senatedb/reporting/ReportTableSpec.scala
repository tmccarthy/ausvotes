package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.State
import au.id.tmm.senatedb.reporting.ReportTable._
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ReportTableSpec extends ImprovedFlatSpec {

  "a report table" can "be rendered as markdown" in {
    val rows = Vector(
      StateRow(State.NSW, 162340, Some(0.0361)),
      StateRow(State.VIC, 49386, Some(0.0141)),
      StateRow(State.QLD, 34730, Some(0.0128)),
      StateRow(State.WA, 21080, Some(0.0154)),
      StateRow(State.SA, 17091, Some(0.0161)),
      StateRow(State.ACT, 2496, Some(0.0074)),
      StateRow(State.TAS, 2249, Some(0.0088)),
      StateRow(State.NT, 1386, Some(0.0136))
    )

    val totalsRow = TotalRow("TOTAL", 290758, Some(0.0210))

    val columns = Vector(
      StateNameColumn,
      TallyColumn("Ballots"),
      FractionColumn()
    )

    val table = ReportTable(rows :+ totalsRow, columns)

    val expectedMarkdown =
      """|State|Ballots|%
        |---|---|---
        |NSW|162,340|3.61%
        |VIC|49,386|1.41%
        |QLD|34,730|1.28%
        |WA|21,080|1.54%
        |SA|17,091|1.61%
        |ACT|2,496|0.74%
        |TAS|2,249|0.88%
        |NT|1,386|1.36%
        |**TOTAL**|**290,758**|**2.10%**""".stripMargin

    assert(table.asMarkdown === expectedMarkdown)
  }

  it should "show a leading 0 for fractions less than 1%" in {
    val rows = Vector(
      StateRow(State.VIC, 1, Some(0.0001))
    )

    val totalsRow = TotalRow("TOTAL", 1, Some(0.0001))

    val columns = Vector(
      StateNameColumn,
      TallyColumn("blah"),
      FractionColumn()
    )

    val table = ReportTable(rows :+ totalsRow, columns)

    val expectedMarkdown =
      """|State|blah|%
        |---|---|---
        |VIC|1|0.01%
        |**TOTAL**|**1**|**0.01%**""".stripMargin

    assert(table.asMarkdown === expectedMarkdown)
  }

}
