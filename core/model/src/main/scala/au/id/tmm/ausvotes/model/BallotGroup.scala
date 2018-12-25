package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.BallotGroup.Code

sealed trait BallotGroup[E] {
  def election: E
  def code: BallotGroup.Code
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

  case object InvalidGroupCode

}

final case class Ungrouped[E](
                               election: E,
                             ) extends BallotGroup[E] {
  val code: BallotGroup.Code = Ungrouped.code
}

object Ungrouped {
  val code: BallotGroup.Code = Code.unsafeMake("UG")
}

object BallotGroup {

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
  }

}
