package au.id.tmm.senatedb.core.rawdata.model

final case class FormalPreferencesRow(electorateName: String,
                                      voteCollectionPointName: String,
                                      voteCollectionPointId: Int,
                                      batchNumber: Int,
                                      paperNumber: Int,
                                      preferences: String
                                     ) extends RawRow {
}
