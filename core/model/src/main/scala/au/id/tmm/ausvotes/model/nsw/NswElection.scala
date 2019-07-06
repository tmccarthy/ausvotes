package au.id.tmm.ausvotes.model.nsw

import java.time.LocalDate

import au.id.tmm.ausvotes.model.Codecs
import au.id.tmm.ausvotes.model.Codecs.Codec
import au.id.tmm.utilities.datetime.LocalDateOrdering

sealed abstract class NswElection(val date: LocalDate, val nswAecId: NswElection.Id) {
  def name: String = s"${date.getYear} election"
}

object NswElection {

  case object `2019` extends NswElection(LocalDate.of(2019, 3, 23), Id("SGE2019"))
  case object `2015` extends NswElection(LocalDate.of(2015, 3, 28), Id("SGE2015"))
  case object `2011` extends NswElection(LocalDate.of(2011, 3, 26), Id("SGE2011"))

  def from(id: Id): Option[NswElection] = id match {
    case `2019`.nswAecId => Some(`2019`)
    case `2015`.nswAecId => Some(`2015`)
    case `2011`.nswAecId => Some(`2011`)
    case _ => None
  }

  final case class Id(asString: String) extends AnyVal
  object Id {
    implicit val codec: Codec[Id] = Codecs.simpleCodec[Id, String](_.asString, Id.apply)
  }

  implicit val codec: Codec[NswElection] = Codecs.partialLiftedCodec[NswElection, Id](_.nswAecId, from)

  implicit val ordering: Ordering[NswElection] =
    Ordering.by[NswElection, LocalDate](_.date)(LocalDateOrdering)
}
