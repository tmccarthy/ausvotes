package au.id.tmm.senatedb.api.persistence.daos.rowentities

import au.id.tmm.senatedb.api.persistence.daos.ElectionDao
import au.id.tmm.senatedb.api.persistence.entities.stats.StatClass
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.utilities.geo.australia.State
import scalikejdbc._

private[daos] final case class StatRow(
                                        id: Long,
                                        statClass: StatClass,
                                        election: SenateElection,
                                        state: Option[State],
                                        division: Option[DivisionRow],
                                        vcp: Option[VoteCollectionPointRow],
                                        amount: Double,
                                        perCapita: Option[Double],
                                        rankRows: List[RankRow] = List(),
                                      )

private[daos] object StatRow extends SQLSyntaxSupport[StatRow] {

  def apply(postcodeFlyweight: PostcodeFlyweight,
            s: SyntaxProvider[StatRow],
            r: SyntaxProvider[RankRow],
            d: SyntaxProvider[DivisionRow],
            v: SyntaxProvider[VoteCollectionPointRow],
            a: SyntaxProvider[AddressRow],
           )(rs: WrappedResultSet): StatRow = {
    apply(
      postcodeFlyweight,
      s.resultName,
      r.resultName,
      d.resultName,
      v.resultName,
      a.resultName,
    )(rs)
  }

  def apply(postcodeFlyweight: PostcodeFlyweight,
            s: ResultName[StatRow],
            r: ResultName[RankRow],
            d: ResultName[DivisionRow],
            v: ResultName[VoteCollectionPointRow],
            a: ResultName[AddressRow],
           )(rs: WrappedResultSet): StatRow = {
    StatRow(
      id = rs.long(s.id),
      statClass = parseStatClass(rs.string(s.statClass)),
      election = ElectionDao.electionWithId(rs.string(s.election)).get,
      state = rs.stringOpt(s.state).flatMap(State.fromAbbreviation),
      division = DivisionRow.opt(d)(rs),
      vcp = VoteCollectionPointRow.opt(postcodeFlyweight, v, d, a)(rs),
      amount = rs.double(s.amount),
      perCapita = rs.doubleOpt(s.perCapita),
    )
  }

  private def parseStatClass(asString: String): StatClass = {
    asString match {
      case "formal_ballots" => StatClass.FormalBallots
      case "donkey_votes" => StatClass.DonkeyVotes
      case "voted_atl" => StatClass.VotedAtl
      case "voted_atl_and_btl" => StatClass.VotedAtlAndBtl
      case "voted_btl" => StatClass.VotedBtl
      case "exhausted_ballots" => StatClass.ExhaustedBallots
      case "exhausted_votes" => StatClass.ExhaustedVotes
      case "used_how_to_vote_card" => StatClass.UsedHowToVoteCard
      case "voted_1_atl" => StatClass.Voted1Atl
      case "used_savings_provision" => StatClass.UsedSavingsProvision
    }
  }
}
