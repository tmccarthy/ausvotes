package au.id.tmm.senatedb.model.stvcount

final case class StvCount(vacancies: Int,
                          totalFormalBallots: Int,
                          quota: Int,
                          candidates: Set[Candidate],
                          steps: List[StvCountStep]) {

}
