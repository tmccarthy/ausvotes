package au.id.tmm.ausvotes.model.federal.senate

import java.time.LocalDate

import au.id.tmm.ausvotes.model.Codecs._
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausvotes.model.federal.senate.SenateElection.SenateElectionType
import au.id.tmm.intime._
import au.id.tmm.utilities.geo.australia.State

sealed trait SenateElection {
  val federalElection: FederalElection

  val senateElectionType: SenateElectionType = SenateElectionType.HalfSenate

  protected def states: Set[State] = State.ALL_STATES

  val allStateElections: Set[SenateElectionForState] = states.map { state =>
    SenateElectionForState.makeUnsafe(this, state)
  }

  private val electionsPerState: Map[State, SenateElectionForState] = allStateElections.map(e => e.state -> e).toMap

  def electionForState(state: State): Option[SenateElectionForState] = electionsPerState.get(state)

  val id: SenateElection.Id

  def date: LocalDate = federalElection.date
  def name: String = federalElection.name
  override def toString: String = name
}

object SenateElection {

  def from(id: SenateElection.Id): Option[SenateElection] = id match {
    case `2016`.id => Some(`2016`)
    case `2014 WA`.id => Some(`2014 WA`)
    case `2013`.id => Some(`2013`)
    case `2010`.id => Some(`2010`)
    case `2007`.id => Some(`2007`)
    case `2004`.id => Some(`2004`)
    case _ => None
  }

  implicit val codec: Codec[SenateElection] = partialLiftedCodec[SenateElection, String](
    encode = _.id.asString,
    decode = s => from(Id(s)),
  )

  implicit val ordering: Ordering[SenateElection] = Ordering.by[SenateElection, LocalDate](_.date)

  case object `2016` extends SenateElection {
    override val federalElection: FederalElection = FederalElection.`2016`
    override val senateElectionType: SenateElectionType = SenateElectionType.FullSenate
    override val id: SenateElection.Id = SenateElection.Id(federalElection.id.asString)
  }

  case object `2014 WA` extends SenateElection {
    override val federalElection: FederalElection = FederalElection.`2014 WA`
    override def states: Set[State] = Set(State.WA)
    override val id: SenateElection.Id = SenateElection.Id(federalElection.id.asString)
  }

  case object `2013` extends SenateElection {
    override val federalElection: FederalElection = FederalElection.`2013`
    override val id: SenateElection.Id = SenateElection.Id(federalElection.id.asString)
  }

  case object `2010` extends SenateElection {
    override val federalElection: FederalElection = FederalElection.`2010`
    override val id: SenateElection.Id = SenateElection.Id(federalElection.id.asString)
  }

  case object `2007` extends SenateElection {
    override val federalElection: FederalElection = FederalElection.`2007`
    override val id: SenateElection.Id = SenateElection.Id(federalElection.id.asString)
  }

  case object `2004` extends SenateElection {
    override val federalElection: FederalElection = FederalElection.`2004`
    override val id: SenateElection.Id = SenateElection.Id(federalElection.id.asString)
  }

  sealed trait SenateElectionType

  object SenateElectionType {
    case object HalfSenate extends SenateElectionType
    case object FullSenate extends SenateElectionType
  }

  final case class Id(asString: String) extends AnyVal

}
