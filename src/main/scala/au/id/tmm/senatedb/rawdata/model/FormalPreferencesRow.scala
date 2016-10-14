package au.id.tmm.senatedb.rawdata.model

final case class FormalPreferencesRow(electorateName: String,
                                      voteCollectionPointName: String,
                                      voteCollectionPointId: Int,
                                      batchNumber: Int,
                                      paperNumber: Int,
                                      preferences: String
                                     ) {
}
