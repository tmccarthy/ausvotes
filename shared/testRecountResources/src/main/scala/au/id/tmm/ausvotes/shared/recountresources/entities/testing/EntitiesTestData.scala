package au.id.tmm.ausvotes.shared.recountresources.entities.testing

import au.id.tmm.ausvotes.core.model.SenateElection.StateAtElection
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, Group}
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates.FetchGroupsAndCandidatesException
import au.id.tmm.utilities.geo.australia.State

final case class EntitiesTestData(
                                   groups: Map[StateAtElection, Set[Group]],
                                   candidates: Map[StateAtElection, Set[Candidate]],
                                 )

object EntitiesTestData {
  def testIOInstance[D](entitiesTestDataField: D => EntitiesTestData): FetchGroupsAndCandidates[TestIO[D, +?, +?]] =
    new FetchGroupsAndCandidates[TestIO[D, +?, +?]] {
      override def fetchGroupsAndCandidatesFor(
                                                election: SenateElection,
                                                state: State,
                                              ): TestIO[D, FetchGroupsAndCandidatesException, GroupsAndCandidates] =
        TestIO { testData =>
          val entitiesTestData = entitiesTestDataField(testData)
          val stateAtElection = (election, state)

          val groupsAndCandidates = GroupsAndCandidates(
            entitiesTestData.groups.getOrElse(stateAtElection, Set.empty),
            entitiesTestData.candidates.getOrElse(stateAtElection, Set.empty),
          )

          TestIO.Output(testData, Right(groupsAndCandidates))
        }
    }
}
