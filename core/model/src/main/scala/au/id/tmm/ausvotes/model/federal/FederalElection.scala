package au.id.tmm.ausvotes.model.federal

import java.time.LocalDate
import java.time.Month._

import au.id.tmm.ausvotes.model.Codecs
import au.id.tmm.ausvotes.model.Codecs.Codec
import au.id.tmm.utilities.datetime.LocalDateOrdering

sealed trait FederalElection {
  val date: LocalDate
  val aecId: FederalElection.AecId

  def id: FederalElection.Id

  def name: String = s"${date.getYear} election"
  override def toString: String = name
}

object FederalElection {

  def from(id: Id): Option[FederalElection] = id match {
    case `2016`.id => Some(`2016`)
    case `2014 WA`.id => Some(`2014 WA`)
    case `2013`.id => Some(`2013`)
    case `2010`.id => Some(`2010`)
    case `2007`.id => Some(`2007`)
    case `2004`.id => Some(`2004`)
    case _ => None
  }

  implicit val ordering: Ordering[FederalElection] = Ordering.by[FederalElection, LocalDate](_.date)(LocalDateOrdering)

  case object `2016` extends FederalElection {
    override val date: LocalDate = LocalDate.of(2016, JULY, 2)
    override val aecId: AecId = AecId(20499)
    override val id: FederalElection.Id = Id("2016")
  }

  case object `2014 WA` extends FederalElection {
    override val date: LocalDate = LocalDate.of(2014, APRIL, 5)
    override val aecId: AecId = AecId(17875)
    override val id: Id = Id("2014WA")
    override val name: String = "2014 WA Senate election"
  }

  case object `2013` extends FederalElection {
    override val date: LocalDate = LocalDate.of(2013, SEPTEMBER, 7)
    override val aecId: AecId = AecId(17496)
    override val id: FederalElection.Id = Id("2013")
  }

  case object `2010` extends FederalElection {
    override val date: LocalDate = LocalDate.of(2010, AUGUST, 21)
    override val aecId: AecId = AecId(15508)
    override val id: FederalElection.Id = Id("2010")
  }

  case object `2007` extends FederalElection {
    override val date: LocalDate = LocalDate.of(2007, NOVEMBER, 24)
    override val aecId: AecId = AecId(13745)
    override val id: FederalElection.Id = Id("2007")
  }

  case object `2004` extends FederalElection {
    override val date: LocalDate = LocalDate.of(2004, OCTOBER, 9)
    override val aecId: AecId = AecId(12246)
    override val id: FederalElection.Id = Id("2004")
  }

  final case class Id(asString: String) extends AnyVal
  final case class AecId(asInt: Int) extends AnyVal

  implicit val codec: Codec[FederalElection] = Codecs.partialLiftedCodec[FederalElection, String](
    encode = _.id.asString,
    decode = rawId => FederalElection.from(Id(rawId)),
  )

}
