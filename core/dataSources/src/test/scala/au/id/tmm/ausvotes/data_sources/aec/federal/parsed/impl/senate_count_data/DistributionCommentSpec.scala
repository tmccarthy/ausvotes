package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DistributionCommentSpec extends ImprovedFlatSpec {

  "a distribution comment" can "indicate a transfer from an excluded candidate" in {
    val rawComment = "Preferences with a transfer value of 1 will be distributed in count # 17 after the exclusion " +
      "of 1 candidate(s). Preferences received at count(s) 1,3,5,7,11,13,15."

    val comment = DistributionComment.from(rawComment)

    assert(comment === Right(DistributionComment.Excluded(1, Set(1, 3, 5, 7, 11, 13, 15), 1.0d)))
  }

  it can "not indicate a transfer from multiple excluded candidates" in {
    val rawComment = "Preferences with a transfer value of 1 will be distributed in count # 17 after the exclusion " +
      "of 2 candidate(s). Preferences received at count(s) 1."

    val comment = DistributionComment.from(rawComment)

    assert(comment === Right(DistributionComment.Excluded(2, Set(1), 1.0d)))
  }

  it can "indicate a transfer from a candidate elected with a surplus" in {
    val rawComment = "GALLAGHER ,K has 10826 surplus vote(s) to be distributed in count # 2 at a transfer value of " +
      "0.113066455002141. 95749 papers are involved from count number(s) 1."

    val comment = DistributionComment.from(rawComment)

    assert(comment === Right(DistributionComment.ElectedWithSurplus(ShortCandidateName("GALLAGHER", 'K'),
      2, Set(1), 0.113066455002141d)))
  }

  it can "indicate a transfer from a candidate elected with a surplus where papers come from more than one count" in {
    val rawComment = "BERNARDI ,C has 176559 surplus vote(s) to be distributed in count # 5 at a transfer value of " +
      "0.520945945945945. 338920 papers are involved from count number(s) 1,2."

    val comment = DistributionComment.from(rawComment)

    assert(comment === Right(DistributionComment.ElectedWithSurplus(ShortCandidateName("BERNARDI", 'C'),
      5, Set(1, 2), 0.520945945945945d)))
  }

  it can "indicate a transfer from a candidate elected with a surplus where the candidate name has an apostrophe" in {
    val rawComment = "O'NEILL ,D has 346702 surplus vote(s) to be distributed in count # 7 at a transfer value of " +
      "0.251436669074410. 1378884 papers are involved from count number(s) 1,2,3,4,5."

    val comment = DistributionComment.from(rawComment)

    assert(comment === Right(DistributionComment.ElectedWithSurplus(ShortCandidateName("O'NEILL" ,'D'),
      7, Set(1, 2, 3, 4, 5), 0.251436669074410d)))
  }

  it can "indicate a transfer from a candidate elected with a surplus where the candidate name has a hyphen" in {
    val rawComment = "KAKOSCHKE-MOORE ,S has 13829 surplus vote(s) to be distributed in count # 456 at a transfer " +
      "value of 0.040791946031568. 339013 papers are involved from count number(s) 1,2,3,4,5."

    val comment = DistributionComment.from(rawComment)

    assert(comment === Right(DistributionComment.ElectedWithSurplus(ShortCandidateName("KAKOSCHKE-MOORE" ,'S'),
      456, Set(1, 2, 3, 4, 5), 0.040791946031568)))
  }

  it can "indicate a transfer from a candidate elected with a surplus where the candidate name has a space" in {
    val rawComment = "DI NATALE ,R has 106685 surplus vote(s) to be distributed in count # 4 at a transfer value of " +
      "0.283785760836314. 375935 papers are involved from count number(s) 1."

    val comment = DistributionComment.from(rawComment)

    assert(comment === Right(DistributionComment.ElectedWithSurplus(ShortCandidateName("DI NATALE" ,'R'),
      4, Set(1), 0.283785760836314d)))
  }

  it can "indicate a candidate was elected without a surplus" in {
    val rawComment = "SESELJA, Z has been elected at count 29 with 85000 votes."

    val comment = DistributionComment.from(rawComment)

    assert(comment === Right(DistributionComment.ElectedWithQuotaNoSurplus(ShortCandidateName("SESELJA", 'Z'))))
  }

  it can "indicate a candidate was elected as the last remaining" in {
    val rawComment = "SCULLION, N, McCARTHY, M have been elected to the remaining positions."

    val comment = DistributionComment.from(rawComment)

    assert(comment === Right(DistributionComment.ElectedLastRemaining(Set(ShortCandidateName("SCULLION", 'N'), ShortCandidateName("McCARTHY", 'M')))))
  }

  "distribution comment parsing" should "throw if an unrecognised comment is provided" in {
    assert(DistributionComment.from("the quick brown fox").left.map(_.getClass) === Left(classOf[IllegalArgumentException]))
  }

}
