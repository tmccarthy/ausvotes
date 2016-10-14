package au.id.tmm.senatedb.rawdata.model

// StateAb	Ticket	CandidateID	BallotPosition	CandidateDetails	PartyName	OrdinaryVotes	AbsentVotes	ProvisionalVotes	PrePollVotes	PostalVotes	TotalVotes
final case class FirstPreferencesRow(state: String,
                                     ticket: String,
                                     candidateId: String,
                                     ballotPosition: Int,
                                     candidateDetails: String,
                                     party: String,
                                     ordinaryVotes: Int,
                                     absentVotes: Int,
                                     provisionalVotes: Int,
                                     prePollVotes: Int,
                                     postalVotes: Int,
                                     totalVotes: Int
                                    ) {
}
