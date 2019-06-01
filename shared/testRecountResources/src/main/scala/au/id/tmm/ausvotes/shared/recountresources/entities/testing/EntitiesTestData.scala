package au.id.tmm.ausvotes.shared.recountresources.entities.testing

import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.FetchSenateGroupsAndCandidates
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.ausvotes.shared.recountresources.CountSummary
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.{FetchCanonicalCountSummary, FetchPreferenceTree}
import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.testing.BState
import au.id.tmm.countstv.model.preferences.PreferenceTree
import cats.instances.list._
import cats.kernel.Monoid
import cats.syntax.traverse._

final case class EntitiesTestData(
                                   basicTestData: BasicTestData = BasicTestData(),

                                   groups: Map[SenateElectionForState, Set[SenateGroup]] = Map.empty,
                                   candidates: Map[SenateElectionForState, Set[SenateCandidate]] = Map.empty,
                                   canonicalCountResults: Map[SenateElectionForState, CountSummary] = Map.empty,
                                   ballots: Map[SenateElectionForState, Vector[Vector[SenateCandidate]]] = Map.empty,
                                 )

object EntitiesTestData {

  type TestIO[+E, +A] = BState[EntitiesTestData, E, A]

  val empty = EntitiesTestData(groups = Map.empty, candidates = Map.empty, canonicalCountResults = Map.empty, ballots = Map.empty)

  trait TestIOInstance[D]
    extends FetchSenateGroupsAndCandidates[BState[D, +?, +?]]
      with FetchCanonicalCountSummary[BState[D, +?, +?]]
      with FetchPreferenceTree[BState[D, +?, +?]]
      with BasicTestData.TestIOInstance[D] {
    protected def entitiesTestDataField(data: D): EntitiesTestData
    override protected def basicTestDataField(data: D): BasicTestData
    override protected def setBasicTestDataField(oldData: D, newBasicTestData: BasicTestData): D

    override def fetchGroupsCandidatesAndPreferencesFor(
                                                         election: SenateElectionForState,
                                                       ): BState[D, FetchPreferenceTree.FetchPreferenceTreeException, FetchPreferenceTree.GroupsCandidatesAndPreferences] = {
      BState { testData =>
        val entitiesTestData = entitiesTestDataField(testData)

        val ballots = entitiesTestData.ballots.getOrElse(election, Vector.empty)

        val groups = entitiesTestData.groups.getOrElse(election, Set.empty)
        val candidates = entitiesTestData.candidates.getOrElse(election, Set.empty)
        val preferenceTree = PreferenceTree.from(candidates)(ballots)

        val groupsCandidatesAndPreferences =
          FetchPreferenceTree.GroupsCandidatesAndPreferences(SenateGroupsAndCandidates(groups, candidates), preferenceTree)

        (testData, Right(groupsCandidatesAndPreferences))
      }
    }

    //noinspection NotImplementedCode
    override def useGroupsCandidatesAndPreferencesWhileCaching[E, A](
                                                                      election: SenateElectionForState,
                                                                    )(
                                                                      handleEntityFetchError: FetchPreferenceTree.FetchPreferenceTreeException => BState[D, E, A],
                                                                      handleCachePopulationError: FetchPreferenceTree.FetchPreferenceTreeException => BState[D, E, Unit],
                                                                    )(
                                                                      action: FetchPreferenceTree.GroupsCandidatesAndPreferences => BState[D, E, A],
                                                                    ): BState[D, E, A] = {
      ???
    }

    override def senateGroupsAndCandidatesFor(
                                               election: SenateElectionForState,
                                             ): BState[D, FetchSenateGroupsAndCandidates.Error, SenateGroupsAndCandidates] =
      BState { testData =>
        val entitiesTestData = entitiesTestDataField(testData)

        val groupsAndCandidates = SenateGroupsAndCandidates(
          entitiesTestData.groups.getOrElse(election, Set.empty),
          entitiesTestData.candidates.getOrElse(election, Set.empty),
        )

        (testData, Right(groupsAndCandidates))
      }

    override def senateGroupsAndCandidatesFor(
                                               election: SenateElection,
                                             ): BState[D, FetchSenateGroupsAndCandidates.Error, SenateGroupsAndCandidates] =
      election.allStateElections.toList
        .traverse(senateGroupsAndCandidatesFor)
        .map(Monoid[SenateGroupsAndCandidates].combineAll)

    override def fetchCanonicalCountSummaryFor(
                                                election: SenateElectionForState,
                                              ): BState[D, FetchCanonicalCountSummary.FetchCanonicalCountSummaryException, CountSummary] =
      BState { testData =>
        val entitiesTestData = entitiesTestDataField(testData)

        //noinspection NotImplementedCode
        (testData, Right(entitiesTestData.canonicalCountResults.getOrElse(election, ???))) // TODO encode the s3 not found behaviour into an error
      }
  }

  implicit val testIOInstance: TestIOInstance[EntitiesTestData] = new TestIOInstance[EntitiesTestData] {
    override protected def entitiesTestDataField(data: EntitiesTestData): EntitiesTestData = data

    override protected def basicTestDataField(data: EntitiesTestData): BasicTestData = data.basicTestData

    override protected def setBasicTestDataField(oldData: EntitiesTestData, newBasicTestData: BasicTestData): EntitiesTestData =
      oldData.copy(basicTestData = newBasicTestData)
  }

}
