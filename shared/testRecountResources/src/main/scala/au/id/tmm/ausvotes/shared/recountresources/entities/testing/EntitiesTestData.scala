package au.id.tmm.ausvotes.shared.recountresources.entities.testing

import au.id.tmm.ausvotes.model.federal.senate.{SenateGroupsAndCandidates, SenateCandidate, SenateElectionForState, SenateGroup}
import au.id.tmm.ausvotes.shared.io.test.{BasicTestData, TestIO}
import au.id.tmm.ausvotes.shared.recountresources.CountSummary
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates.FetchGroupsAndCandidatesException
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.{FetchCanonicalCountSummary, FetchGroupsAndCandidates, FetchPreferenceTree}
import au.id.tmm.countstv.model.preferences.PreferenceTree

final case class EntitiesTestData(
                                   basicTestData: BasicTestData = BasicTestData(),

                                   groups: Map[SenateElectionForState, Set[SenateGroup]] = Map.empty,
                                   candidates: Map[SenateElectionForState, Set[SenateCandidate]] = Map.empty,
                                   canonicalCountResults: Map[SenateElectionForState, CountSummary] = Map.empty,
                                   ballots: Map[SenateElectionForState, Vector[Vector[SenateCandidate]]] = Map.empty,
                                 )

object EntitiesTestData {

  val empty = EntitiesTestData(groups = Map.empty, candidates = Map.empty, canonicalCountResults = Map.empty, ballots = Map.empty)

  trait TestIOInstance[D]
    extends FetchGroupsAndCandidates[TestIO[D, +?, +?]]
      with FetchCanonicalCountSummary[TestIO[D, +?, +?]]
      with FetchPreferenceTree[TestIO[D, +?, +?]]
      with BasicTestData.TestIOInstance[D] {
    protected def entitiesTestDataField(data: D): EntitiesTestData
    override protected def basicTestDataField(data: D): BasicTestData
    override protected def setBasicTestDataField(oldData: D, newBasicTestData: BasicTestData): D

    override def fetchGroupsCandidatesAndPreferencesFor(
                                                         election: SenateElectionForState,
                                                       ): TestIO[D, FetchPreferenceTree.FetchPreferenceTreeException, FetchPreferenceTree.GroupsCandidatesAndPreferences] = {
      TestIO { testData =>
        val entitiesTestData = entitiesTestDataField(testData)

        val ballots = entitiesTestData.ballots.getOrElse(election, Vector.empty)

        val groups = entitiesTestData.groups.getOrElse(election, Set.empty)
        val candidates = entitiesTestData.candidates.getOrElse(election, Set.empty)
        val preferenceTree = PreferenceTree.from(candidates)(ballots)

        val groupsCandidatesAndPreferences =
          FetchPreferenceTree.GroupsCandidatesAndPreferences(SenateGroupsAndCandidates(groups, candidates), preferenceTree)

        TestIO.Output(testData, Right(groupsCandidatesAndPreferences))
      }
    }

    //noinspection NotImplementedCode
    override def useGroupsCandidatesAndPreferencesWhileCaching[E, A](
                                                                      election: SenateElectionForState,
                                                                    )(
                                                                      handleEntityFetchError: FetchPreferenceTree.FetchPreferenceTreeException => TestIO[D, E, A],
                                                                      handleCachePopulationError: FetchPreferenceTree.FetchPreferenceTreeException => TestIO[D, E, Unit],
                                                                    )(
                                                                      action: FetchPreferenceTree.GroupsCandidatesAndPreferences => TestIO[D, E, A],
                                                                    ): TestIO[D, E, A] = {
      ???
    }

    override def fetchGroupsAndCandidatesFor(
                                              election: SenateElectionForState,
                                            ): TestIO[D, FetchGroupsAndCandidatesException, SenateGroupsAndCandidates] =
      TestIO { testData =>
        val entitiesTestData = entitiesTestDataField(testData)

        val groupsAndCandidates = SenateGroupsAndCandidates(
          entitiesTestData.groups.getOrElse(election, Set.empty),
          entitiesTestData.candidates.getOrElse(election, Set.empty),
        )

        TestIO.Output(testData, Right(groupsAndCandidates))
      }

    override def fetchCanonicalCountSummaryFor(
                                                election: SenateElectionForState,
                                             ): TestIO[D, FetchCanonicalCountSummary.FetchCanonicalCountSummaryException, CountSummary] =
      TestIO { testData =>
        val entitiesTestData = entitiesTestDataField(testData)

        //noinspection NotImplementedCode
        TestIO.Output(testData, Right(entitiesTestData.canonicalCountResults.getOrElse(election, ???))) // TODO encode the s3 not found behaviour into an error
      }
  }

  implicit val testIOInstance: TestIOInstance[EntitiesTestData] = new TestIOInstance[EntitiesTestData] {
    override protected def entitiesTestDataField(data: EntitiesTestData): EntitiesTestData = data

    override protected def basicTestDataField(data: EntitiesTestData): BasicTestData = data.basicTestData

    override protected def setBasicTestDataField(oldData: EntitiesTestData, newBasicTestData: BasicTestData): EntitiesTestData =
      oldData.copy(basicTestData = newBasicTestData)
  }

}
