package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import au.id.tmm.ausvotes.core.fixtures.{CandidateFixture, DivisionAndPollingPlaceFixture, GroupAndCandidateFixture}
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.ausvotes.shared.aws.data.{ContentType, S3BucketName, S3ObjectKey}
import au.id.tmm.ausvotes.shared.aws.testing.AwsTestData
import au.id.tmm.ausvotes.shared.aws.testing.AwsTestData.AwsTestIO
import au.id.tmm.ausvotes.shared.aws.testing.testdata.S3TestData
import au.id.tmm.ausvotes.shared.recountresources.CountSummary
import au.id.tmm.ausvotes.tasks.generatepreferencetrees.DataBundleConstruction.DataBundleForElection
import au.id.tmm.countstv.model.preferences.PreferenceTree
import au.id.tmm.countstv.model.values.{Count, Ordinal}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateStatuses, VoteCount}
import au.id.tmm.utilities.collection.DupelessSeq
import au.id.tmm.ausgeo.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import org.scalatest.FlatSpec
import io.circe.syntax.EncoderOps

class DataBundleWritingSpec extends FlatSpec {

  "the writing of a data bundle" should "write the canonical recount file" in {
    import CandidateFixture.ACT._

    val s3BucketName = S3BucketName("bucketName")

    val recountResult = CountSummary(
      CountSummary.Request(
        SenateElection.`2016`.electionForState(State.ACT).get,
        2,
        ineligibleCandidates = Set(
          zedSeselja,
        ),
        doRounding = true,
      ),
      outcomePossibilities = ProbabilityMeasure.Always(
        CountSummary.Outcome(
          elected = DupelessSeq(katyGallagher),
          exhaustedVotes = VoteCount.zero,
          roundingError = VoteCount.zero,
          candidateOutcomes =  CandidateStatuses(
            CandidateFixture.ACT.candidates.map {
              case c if c == katyGallagher => c -> CandidateStatus.Elected(Ordinal.first, Count(2))
              case c if c == janeHiatt => c -> CandidateStatus.Elected(Ordinal.second, Count(3))
              case c if c == zedSeselja => c -> CandidateStatus.Ineligible
              case c => c -> CandidateStatus.Remaining
            }.toMap
          ),
        ),
      )
    )

    val dataBundleToWrite = DataBundleForElection(
      SenateElection.`2016`.electionForState(State.ACT).get,
      GroupAndCandidateFixture.ACT.groupsAndCandidates,
      DivisionAndPollingPlaceFixture.ACT.divisionsAndPollingPlaces,
      recountResult,
      PreferenceTree.from(CandidateFixture.ACT.candidates)(Iterable.empty),
    )

    val testLogic = DataBundleWriting.writeToS3Bucket[AwsTestIO](s3BucketName, dataBundleToWrite)

    val s3 = S3TestData.InMemoryS3().addBucket(s3BucketName)
    val testData = AwsTestData(s3TestData = S3TestData(s3))

    val testDataAfterWrite = testLogic.runS(testData)

    val writtenRecountResult = for {
      recountBucket <- testDataAfterWrite.s3TestData.s3Content(s3BucketName)
      recountResultObject <- recountBucket(S3ObjectKey("recountData", "2016", "ACT", "canonicalRecountResult.json"))
    } yield recountResultObject

    assert(writtenRecountResult.map(_.content) === Some(recountResult.asJson.toString))
    assert(writtenRecountResult.map(_.contentType) === Some(ContentType.APPLICATION_JSON))
  }

}
