package au.id.tmm.senatedb.parsing

import au.id.tmm.senatedb.rawdata.model.RawRow
import au.id.tmm.utilities.geo.australia.State

object GenerationUtils {

  def stateFrom(rawState: String, rawRow: RawRow): State = {
    State.fromAbbreviation(rawState.trim)
      .getOrElse(throw new BadDataException(s"Encountered bad state value $rawState in row $rawRow"))
  }

}
