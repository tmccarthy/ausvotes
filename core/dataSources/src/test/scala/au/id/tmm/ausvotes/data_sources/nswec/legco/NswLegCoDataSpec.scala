package au.id.tmm.ausvotes.data_sources.nswec.legco

import au.id.tmm.ausvotes.data_sources.nswec.legco.NswLegCoStreams.SpreadsheetCell
import au.id.tmm.ausvotes.model.CandidateDetails.{Id => CandidateId}
import au.id.tmm.ausvotes.model.nsw.NswElection
import au.id.tmm.ausvotes.model.nsw.legco._
import au.id.tmm.ausvotes.model.stv.BallotGroup
import au.id.tmm.ausvotes.model.{Name, Party}
import au.id.tmm.bfect.fs2interop._
import au.id.tmm.bfect.testing.BState
import org.scalatest.FlatSpec

class NswLegCoDataSpec extends FlatSpec {

  private type TestIO[+E, +A] = BState.Stateless[E, A]

  private type TestStream[+A] = fs2.Stream[TestIO[Throwable, +?], A]

  private def makeSpreadsheet(rows: Vector[Any]*): TestStream[Vector[SpreadsheetCell]] =
    fs2.Stream.emits(rows.map { row =>
      row.map {
        case null      => SpreadsheetCell.Empty
        case s: String => SpreadsheetCell.WithString(s)
        case d: Double => SpreadsheetCell.WithDouble(d)
        case i: Int    => SpreadsheetCell.WithDouble(i)
      }
    })

  private val mockStreams: NswLegCoStreams[TestIO] = new NswLegCoStreams[TestIO] {
    override def groupRows(election: NswLegCoElection): TestStream[Vector[SpreadsheetCell]] =
      makeSpreadsheet(
        Vector("GVS", "Group", "Group/Candidates in Ballot Order"),
        Vector("Yes", "A",     "Party A"),
        Vector("No",  "B",     ""),
        Vector("No",  "C",     null),
      )

    override def candidateRows(election: NswLegCoElection): TestStream[Vector[SpreadsheetCell]] =
      makeSpreadsheet(
        Vector("In Grp Seq.", "GVS/Candidate", "Group", "Group/Candidates in Ballot Order"),
        Vector(1,             "Candidate",     "A",     "DOE Jane"),
        Vector(2,             "Candidate",     "A",     "McDONNELL Joe"),
        Vector(1,             "Candidate",     "B",     "SMITH-ADAMS Fred"),
        Vector(2,             "Candidate",     "B",     "ALLEN Barbara"),
      )

    override def preferenceRows(election: NswLegCoElection): TestStream[Vector[String]] = fs2.Stream.empty
  }

  private val sut = new NswLegCoData.Live[TestIO](mockStreams)

  private val election = NswLegCoElection(NswElection.`2019`)

  "parsing the groups for a NSW legco election" should "work" in {
    val expectedGroups = Set(
      Group(election, BallotGroup.Code("A").right.get, Some(Party("Party A"))).right.get,
      Group(election, BallotGroup.Code("B").right.get, None).right.get,
      Group(election, BallotGroup.Code("C").right.get, None).right.get,
    )

    assert(sut.fetchGroupsAndCandidatesFor(election).statelessRunUnsafe.groups === expectedGroups)
  }

  "parsing the candidates for a NSW legco election" should "work" in {
    val groupA = Group(election, BallotGroup.Code("A").right.get, Some(Party("Party A"))).right.get
    val groupB = Group(election, BallotGroup.Code("B").right.get, None).right.get

    val expectedCandidates = Set(
      Candidate(election, CandidateDetails(election, Name("Jane", "DOE"),         Some(Party("Party A")), CandidateId(0)), CandidatePosition(groupA, 0)),
      Candidate(election, CandidateDetails(election, Name("Joe", "McDONNELL"),    Some(Party("Party A")), CandidateId(1)), CandidatePosition(groupA, 1)),
      Candidate(election, CandidateDetails(election, Name("Fred", "SMITH-ADAMS"), None,                   CandidateId(2)), CandidatePosition(groupB, 0)),
      Candidate(election, CandidateDetails(election, Name("Barbara", "ALLEN"),    None,                   CandidateId(3)), CandidatePosition(groupB, 1)),
    )

    assert(sut.fetchGroupsAndCandidatesFor(election).statelessRunUnsafe.candidates === expectedCandidates)
  }

}
