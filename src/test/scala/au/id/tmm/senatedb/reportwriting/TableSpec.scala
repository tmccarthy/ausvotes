package au.id.tmm.senatedb.reportwriting

import au.id.tmm.senatedb.reportwriting.table.Table.StringIterableOps
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class TableSpec extends ImprovedFlatSpec {
  "a table row" should "start and end with '|' when converted to markdown" in {
    assert(Vector("the", "quick", "brown", "fox").mkMarkdownRow === "|the|quick|brown|fox|")
  }

  it should "start with '|' when the first cell is blank when converted to markdown" in {
    assert(Vector("", "the", "quick", "brown", "fox").mkMarkdownRow === "| |the|quick|brown|fox|")
  }
}
