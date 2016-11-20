package au.id.tmm.senatedb.core.rawdata.model

final case class PollingPlacesRow(state: String,
                                  divisionId: Int,
                                  divisionName: String,
                                  pollingPlaceId: Int,
                                  pollingPlaceTypeId: Int,
                                  pollingPlaceName: String,
                                  premisesName: String,
                                  premisesAddress1: String,
                                  premisesAddress2: String,
                                  premisesAddress3: String,
                                  premisesSuburb: String,
                                  premisesState: String,
                                  premisesPostcode: String,
                                  latitude: Option[Double],
                                  longitude: Option[Double]
                                 ) extends RawRow {
}
