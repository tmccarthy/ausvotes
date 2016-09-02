package au.id.tmm.senatedb.model.stvcount

final case class Candidate(ballotPosition: Int,
                           group: String,
                           positionInGroup: Int,
                           surname: String,
                           givenName: String)
