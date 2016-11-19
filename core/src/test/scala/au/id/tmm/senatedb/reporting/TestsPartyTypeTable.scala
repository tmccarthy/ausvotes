package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.reporting.TableBuilders.{NationalPerFirstPrefTableBuilder, NationallyPerPartyTypeTableBuilder}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

trait TestsPartyTypeTable { this: TestsStandardReportBuilder with ImprovedFlatSpec =>
  it should "include a table by first preferenced party type after the first preferences table" in {
    val indexOfFirstPrefTable = sut.tableBuilders.indexWhere(_.isInstanceOf[NationalPerFirstPrefTableBuilder])
    val indexOfFirstPrefTypeTable = sut.tableBuilders.indexWhere(_.isInstanceOf[NationallyPerPartyTypeTableBuilder])

    assert(indexOfFirstPrefTypeTable === indexOfFirstPrefTable + 1)
  }
}
