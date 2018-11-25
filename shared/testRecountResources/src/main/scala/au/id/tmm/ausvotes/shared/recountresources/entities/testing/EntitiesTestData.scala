package au.id.tmm.ausvotes.shared.recountresources.entities.testing

import au.id.tmm.ausvotes.core.model.SenateElection.StateAtElection
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, Group}
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.recountresources.CountResult
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates.FetchGroupsAndCandidatesException
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.{FetchCanonicalCountResult, FetchGroupsAndCandidates}
import au.id.tmm.utilities.geo.australia.State

final case class EntitiesTestData(
                                   groups: Map[StateAtElection, Set[Group]] = Map.empty,
                                   candidates: Map[StateAtElection, Set[Candidate]] = Map.empty,
                                   canonicalCountResults: Map[StateAtElection, CountResult] = Map.empty,
                                 )

object EntitiesTestData {

  val empty = EntitiesTestData(groups = Map.empty, candidates = Map.empty, canonicalCountResults = Map.empty)

  trait TestIOInstance[D]
    extends FetchGroupsAndCandidates[TestIO[D, +?, +?]]
      with FetchCanonicalCountResult[TestIO[D, +?, +?]] {
    protected def entitiesTestDataField(data: D): EntitiesTestData

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

    override def fetchCanonicalCountResultFor(
                                               election: SenateElection,
                                               state: State,
                                             ): TestIO[D, FetchCanonicalCountResult.FetchCanonicalCountResultException, CountResult] =
      TestIO { testData =>
        val entitiesTestData = entitiesTestDataField(testData)
        val stateAtElection = (election, state)

        TestIO.Output(testData, Right(entitiesTestData.canonicalCountResults.getOrElse(stateAtElection, ???))) // TODO encode the s3 not found behaviour into an error
      }
  }
}
