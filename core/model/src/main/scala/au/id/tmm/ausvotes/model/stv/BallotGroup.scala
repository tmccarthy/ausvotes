package au.id.tmm.ausvotes.model.stv

import au.id.tmm.ausvotes.model.Codecs.Codec
import au.id.tmm.ausvotes.model._
import au.id.tmm.ausvotes.model.stv.BallotGroup.Code
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, DecodingFailure, Encoder, Json}

sealed trait BallotGroup[E] {
  def election: E
  def code: BallotGroup.Code
}

object BallotGroup {

  implicit def ordering[E : Ordering]: Ordering[BallotGroup[E]] = Ordering.by(g => (g.election, g.code.index))

  implicit def encoder[E : Encoder]: Encoder[BallotGroup[E]] = { group =>
    val baseJson = Json.obj(
      "election" -> group.election.asJson,
      "code" -> group.code.asJson,
    )

    group match {
      case Group(_, _, party) => baseJson.deepMerge(Json.obj("party" -> party.asJson))
      case Ungrouped(_) => baseJson
    }
  }

  implicit def decoder[E : Decoder]: Decoder[BallotGroup[E]] = c =>
    for {
      election <- c.get[E]("election")
      code <- c.get[Code]("code")
      party <- c.get[Option[Party]]("party")
      ballotGroup <- code match {
        case Ungrouped.code => Right(Ungrouped(election))
        case code => Group(election, code, party).left.map(_ => DecodingFailure("Invalid group code", c.history))
      }
    } yield ballotGroup

  final case class Code private (asString: String) extends AnyVal {
    def index: Int = {
      def charValue(char: Char) = char.toUpper - 'A'

      if (asString.length == 1) {
        charValue(asString.charAt(0))
      } else if (asString == "UG") {
        Int.MaxValue
      } else {
        (26 * (1 + charValue(asString.charAt(0)))) + charValue(asString.charAt(1))
      }
    }
  }

  object Code {

    private val validCodePattern = "^[A-Z]{1,2}$".r

    def apply(asString: String): Either[InvalidCode, Code] = asString match {
      case validCodePattern() => Right(new Code(asString))
      case invalidCode => Left(InvalidCode(invalidCode))
    }

    private[model] def unsafeMake(asString: String): Code = Code(asString).right.get

    final case class InvalidCode(invalidCode: String) extends ExceptionCaseClass

    implicit val codec: Codec[Code] = Codecs.partialCodec[Code, String](
      encode = _.asString,
      decode = Code(_).toOption,
    )

  }

}

final case class Group[E] private (
                                    election: E,
                                    code: BallotGroup.Code,
                                    party: Option[Party],
                                  ) extends BallotGroup[E]

object Group {

  def apply[E](
                election: E,
                code: Code,
                party: Option[Party],
              ): Either[InvalidGroupCode.type, Group[E]] = code match {
    case Ungrouped.code => Left(InvalidGroupCode)
    case code => Right(new Group(election, code, party))
  }

  case object InvalidGroupCode extends ExceptionCaseClass

}

final case class Ungrouped[E](
                               election: E,
                             ) extends BallotGroup[E] {
  val code: BallotGroup.Code = Ungrouped.code
}

object Ungrouped {
  val code: BallotGroup.Code = Code.unsafeMake("UG")
}
