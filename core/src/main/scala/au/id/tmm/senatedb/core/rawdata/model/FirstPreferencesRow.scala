package au.id.tmm.senatedb.core.rawdata.model

final case class FirstPreferencesRow(state: String,
                                     ticket: String,
                                     candidateId: String,
                                     positionInGroup: Int,
                                     candidateDetails: String,
                                     party: String,
                                     ordinaryVotes: Int,
                                     absentVotes: Int,
                                     provisionalVotes: Int,
                                     prePollVotes: Int,
                                     postalVotes: Int,
                                     totalVotes: Int
                                    ) extends RawRow {
}
