package au.id.tmm.senatedb.parsing.countdata

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DistributionCommentSpec extends ImprovedFlatSpec {

  "a distribution comment" can "indicate a transfer from an excluded candidate" in {
    val rawComment = "Preferences with a transfer value of 1 will be distributed in count # 17 after the exclusion " +
      "of 1 candidate(s). Preferences received at count(s) 1,3,5,7,11,13,15."

    val comment = DistributionComment.from(rawComment)

    assert(comment === DistributionComment.Excluded(1, Set(1, 3, 5, 7, 11, 13, 15), 1.0d))
  }

  it can "not indicate a transfer from multiple excluded candidates" in {
    val rawComment = "Preferences with a transfer value of 1 will be distributed in count # 17 after the exclusion " +
      "of 2 candidate(s). Preferences received at count(s) 1."

    val comment = DistributionComment.from(rawComment)

    assert(comment === DistributionComment.Excluded(2, Set(1), 1.0d))
  }

  it can "indicate a transfer from a candidate elected with a surplus" in {
    val rawComment = "GALLAGHER ,K has 10826 surplus vote(s) to be distributed in count # 2 at a transfer value of " +
      "0.113066455002141. 95749 papers are involved from count number(s) 1."

    val comment = DistributionComment.from(rawComment)

    assert(comment === DistributionComment.ElectedWithSurplus(ShortCandidateName("GALLAGHER", 'K'), 2, Set(1), 0.113066455002141d))
  }

  it can "indicate a candidate was elected without a surplus" in {
    val rawComment = "SESELJA, Z has been elected at count 29 with 85000 votes."

    val comment = DistributionComment.from(rawComment)

    assert(comment === DistributionComment.ElectedWithQuotaNoSurplus(ShortCandidateName("SESELJA", 'Z')))
  }

  it can "indicate a candidate was elected as the last remaining" in {
    val rawComment = "SCULLION, N, McCARTHY, M have been elected to the remaining positions."

    val comment = DistributionComment.from(rawComment)

    assert(comment === DistributionComment.ElectedLastRemaining(Set(ShortCandidateName("SCULLION", 'N'), ShortCandidateName("McCARTHY", 'M'))))
  }

  "distribution comment parsing" should "throw if an unrecognised comment is provided" in {
    intercept[IllegalArgumentException](DistributionComment.from("the quick brown fox"))
  }

}
