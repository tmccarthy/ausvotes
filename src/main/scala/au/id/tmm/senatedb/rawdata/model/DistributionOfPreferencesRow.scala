package au.id.tmm.senatedb.rawdata.model

final case class DistributionOfPreferencesRow(state: String,
                                              numberOfVacancies: Int,
                                              totalFormalPapers: Int,
                                              quota: Int,
                                              count: Int,
                                              ballotPosition: Int,
                                              ticket: String,
                                              surname: String,
                                              givenName: String,
                                              papers: Int,
                                              votesTransferred: Int,
                                              progressiveVoteTotal: Int,
                                              transferValue: Double,
                                              status: String,
                                              changed: Option[Boolean],
                                              orderElected: Int,
                                              comment: String
                                             ) extends RawRow {
}
