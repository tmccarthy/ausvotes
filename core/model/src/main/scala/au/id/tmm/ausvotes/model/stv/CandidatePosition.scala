package au.id.tmm.ausvotes.model.stv

import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}

import scala.util.Try

final case class CandidatePosition[E](
                                       group: BallotGroup[E],
                                       indexInGroup: Int,
                                     )

object CandidatePosition {

  implicit def ordering[E : Ordering]: Ordering[CandidatePosition[E]] = Ordering.by(p => (p.group, p.indexInGroup))

  implicit def encoder[E]: Encoder[CandidatePosition[E]] = p => s"${p.group.code.asString}${p.indexInGroup}".asJson

  private val positionPattern = "^([A-Z]{1,2})(\\d+)$".r

  def decoderUsing[E](allGroups: Iterable[Group[E]], ungrouped: => Ungrouped[E]): Decoder[CandidatePosition[E]] = {
    val lookup = allGroups.groupBy(_.code)
      .map { case (code, groups) => code -> groups.head }

    decoder(lookup.lift, ungrouped)
  }

  def decoder[E](groupFromCode: BallotGroup.Code => Option[Group[E]], ungrouped: => Ungrouped[E]): Decoder[CandidatePosition[E]] =
    Decoder.decodeString.emap {
      case positionPattern(rawCode, rawIndexInGroup) =>
        for {
          code <- BallotGroup.Code(rawCode)
              .left.map(_ => "Invalid code")

          group <- code match {
            case Ungrouped.code => Right(ungrouped)
            case code => groupFromCode(code)
              .toRight(s"""No such group "${code.asString}"""")
          }

          indexInGroup <- Try(rawIndexInGroup.toInt).toEither
              .left.map(_.getMessage)
        } yield CandidatePosition(group, indexInGroup)

      case _ => Left("Invalid code")
    }

}
