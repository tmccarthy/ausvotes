package au.id.tmm.ausvotes.shared.recountresources.entities.testing

import au.id.tmm.ausvotes.core.model.SenateElection.StateAtElection
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, Group}
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.io.test.{BasicTestData, TestIO}
import au.id.tmm.ausvotes.shared.recountresources.CountResult
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates.FetchGroupsAndCandidatesException
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.{FetchCanonicalCountResult, FetchGroupsAndCandidates, FetchPreferenceTree}
import au.id.tmm.countstv.model.preferences.PreferenceTree
import au.id.tmm.utilities.geo.australia.State

final case class EntitiesTestData(
                                   basicTestData: BasicTestData = BasicTestData(),

                                   groups: Map[StateAtElection, Set[Group]] = Map.empty,
                                   candidates: Map[StateAtElection, Set[Candidate]] = Map.empty,
                                   canonicalCountResults: Map[StateAtElection, CountResult] = Map.empty,
                                   ballots: Map[StateAtElection, Vector[Vector[Candidate]]] = Map.empty,
                                 )

object EntitiesTestData {

  val empty = EntitiesTestData(groups = Map.empty, candidates = Map.empty, canonicalCountResults = Map.empty, ballots = Map.empty)

  trait TestIOInstance[D]
    extends FetchGroupsAndCandidates[TestIO[D, +?, +?]]
      with FetchCanonicalCountResult[TestIO[D, +?, +?]]
      with FetchPreferenceTree[TestIO[D, +?, +?]]
      with BasicTestData.TestIOInstance[D] {
    protected def entitiesTestDataField(data: D): EntitiesTestData
    override protected def basicTestDataField(data: D): BasicTestData
    override protected def setBasicTestDataField(oldData: D, newBasicTestData: BasicTestData): D

    override def fetchGroupsCandidatesAndPreferencesFor(
                                                         election: SenateElection,
                                                         state: State,
                                                       ): TestIO[D, FetchPreferenceTree.FetchPreferenceTreeException, FetchPreferenceTree.GroupsCandidatesAndPreferences] = {
      TestIO { testData =>
        val entitiesTestData = entitiesTestDataField(testData)
        val stateAtElection = (election, state)

        val ballots = entitiesTestData.ballots.getOrElse(stateAtElection, Vector.empty)

        val groups = entitiesTestData.groups.getOrElse(stateAtElection, Set.empty)
        val candidates = entitiesTestData.candidates.getOrElse(stateAtElection, Set.empty)
        val preferenceTree = PreferenceTree.from(candidates)(ballots)

        val groupsCandidatesAndPreferences =
          FetchPreferenceTree.GroupsCandidatesAndPreferences(GroupsAndCandidates(groups, candidates), preferenceTree)

        TestIO.Output(testData, Right(groupsCandidatesAndPreferences))
      }
    }

    //noinspection NotImplementedCode
    override def useGroupsCandidatesAndPreferencesWhileCaching[E, A](
                                                                      election: SenateElection,
                                                                      state: State,
                                                                    )(
                                                                      handleEntityFetchError: FetchPreferenceTree.FetchPreferenceTreeException => TestIO[D, E, A],
                                                                      handleCachePopulationError: FetchPreferenceTree.FetchPreferenceTreeException => TestIO[D, E, Unit],
                                                                    )(
                                                                      action: FetchPreferenceTree.GroupsCandidatesAndPreferences => TestIO[D, E, A],
                                                                    ): TestIO[D, E, A] = {
      ???
    }

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

  implicit val testIOInstance: TestIOInstance[EntitiesTestData] = new TestIOInstance[EntitiesTestData] {
    override protected def entitiesTestDataField(data: EntitiesTestData): EntitiesTestData = data

    override protected def basicTestDataField(data: EntitiesTestData): BasicTestData = data.basicTestData

    override protected def setBasicTestDataField(oldData: EntitiesTestData, newBasicTestData: BasicTestData): EntitiesTestData =
      oldData.copy(basicTestData = newBasicTestData)
  }

}
