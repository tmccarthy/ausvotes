package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.stv.{BallotGroup, Group}
import au.id.tmm.utilities.collection.Flyweight

// TODO should this sit in the model?
object Flyweights {

  final class ElectorateFlyweight[E] private () {
    private val underlying: Flyweight[(E, String, Electorate.Id), Electorate[E]] = Flyweight {
      case (election, name, id) => Electorate(election, name, id)
    }

    def make(
              election: E,
              name: String,
              id: Electorate.Id,
            ): Electorate[E] = underlying((election, name, id))
  }

  object ElectorateFlyweight {
    def apply[E](): ElectorateFlyweight[E] = new ElectorateFlyweight()
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
