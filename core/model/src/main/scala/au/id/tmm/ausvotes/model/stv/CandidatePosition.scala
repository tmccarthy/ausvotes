package au.id.tmm.ausvotes.model.stv

import au.id.tmm.ausvotes.model.Codecs
import au.id.tmm.ausvotes.model.Codecs.Codec

import scala.util.Try

// TODO give some thought as to whether this is needed at this level of abstraction
final case class CandidatePosition(
                                    groupCode: BallotGroup.Code,
                                    indexInGroup: Int,
                                  )

object CandidatePosition {

  private val positionPattern = "^([A-Z]{1,2})(\\d+)$".r

  implicit val codec: Codec[CandidatePosition] = Codecs.partialCodec[CandidatePosition, String](
    encode = p => s"${p.groupCode.asString}${p.indexInGroup}",
    decode = {
      case positionPattern(rawCode, rawIndexInGroup) =>
        for {
          code <- BallotGroup.Code(rawCode).toOption
          indexInGroup <- Try(rawIndexInGroup.toInt).toOption
        } yield CandidatePosition(code, indexInGroup)
      case _ => None
    }
  )

}
