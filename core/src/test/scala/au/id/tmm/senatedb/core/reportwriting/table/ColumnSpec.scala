package au.id.tmm.senatedb.core.reportwriting.table

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ColumnSpec extends ImprovedFlatSpec {

  "the empty column" should "not render anything for any key" in {
    assert(Column.EmptyColumn.valueFor(Row.DataRow(Unit, 0, 0, 0)) === "")
  }

}
