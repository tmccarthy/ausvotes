package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences

import au.id.tmm.senatedb.data.CandidatePosition

private[this] final class DopCsvRow(private val row: Seq[String], val candidatePosition: Option[CandidatePosition]) {
  lazy val state = row(0).trim
  lazy val numVacancies = row(1).toInt
  lazy val totalFormalPapers = row(2).toInt
  lazy val quota = row(3).toInt
  lazy val count = row(4).toInt
  lazy val ballotPosition = row(5).toInt
  lazy val group = row(6).trim
  lazy val surname = row(7).trim
  lazy val givenName = row(8).trim
  lazy val papers = row(9).toInt
  lazy val votesTransferred = row(10).toInt
  lazy val progressiveVoteTotal = row(11).toInt
  lazy val transferValue = row(12).toDouble
  lazy val status = CandidateStatus.parsedFrom(row(13).trim)
  lazy val statusChanged = {
    val raw = row(14).trim

    if (raw.isEmpty) false else raw.toBoolean
  }
  lazy val orderElected = row(15).toInt
  lazy val comment = row(16).trim
}