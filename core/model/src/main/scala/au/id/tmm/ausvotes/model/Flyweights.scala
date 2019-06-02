package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.stv.{BallotGroup, Group}
import au.id.tmm.utilities.collection.Flyweight

// TODO should this sit in the model?
object Flyweights {

  final class ElectorateFlyweight[E, J] private () {
    private val underlying: Flyweight[(E, J, String), Electorate[E, J]] = Flyweight {
      case (election, jurisdiction, name) => Electorate(election, jurisdiction, name)
    }

    def make(
              election: E,
              jurisdiction: J,
              name: String,
            ): Electorate[E, J] = underlying((election, jurisdiction, name))
  }

  object ElectorateFlyweight {
    def apply[E, J](): ElectorateFlyweight[E, J] = new ElectorateFlyweight()
  }

  final class GroupFlyweight[E] private () {
    private val underlying: Flyweight[(E, BallotGroup.Code, Option[Party]), Either[Group.InvalidGroupCode.type, Group[E]]] = Flyweight {
      case (election, code, party) => Group(election, code, party)
    }

    def make(
              election: E,
              code: BallotGroup.Code,
              party: Option[Party],
            ): Either[Group.InvalidGroupCode.type, Group[E]] = underlying((election, code, party))
  }

  object GroupFlyweight {
    def apply[E](): GroupFlyweight[E] = new GroupFlyweight()
  }

}
