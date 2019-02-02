package au.id.tmm.ausvotes.analysis

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class JoinsSpec extends ImprovedFlatSpec {

  "an inner join" should "return no elements when there are no matches" in {
    val lefts = List(
      "a" -> 1,
      "b" -> 2,
    )

    val rights = List(
      "c" -> 3,
      "d" -> 4,
    )

    val expectedJoined = Nil

    assert(Joins.innerJoin(lefts, rights)(_._1, _._1) === expectedJoined)
  }

  it should "return all elements when they all match" in {
    val lefts = List(
      "a" -> 1,
      "b" -> 2,
    )

    val rights = List(
      "a" -> 3,
      "b" -> 4,
    )

    val expectedJoined = List(
      ("a", "a" -> 1, "a" -> 3),
      ("b", "b" -> 2, "b" -> 4),
    )

    assert(Joins.innerJoin(lefts, rights)(_._1, _._1) === expectedJoined)
  }

  it should "return only matching elements if some do not match" in {
    val lefts = List(
      "a" -> 1,
      "b" -> 2,
      "c" -> 3,
    )

    val rights = List(
      "b" -> 4,
      "c" -> 5,
      "d" -> 6,
    )

    val expectedJoined = List(
      ("b", "b" -> 2, "b" -> 4),
      ("c", "c" -> 3, "c" -> 5),
    )

    assert(Joins.innerJoin(lefts, rights)(_._1, _._1) === expectedJoined)
  }

  it should "duplicate rows if there is a duplicate left match" in {
    val lefts = List(
      "a" -> 1,
      "a" -> 10,
      "b" -> 2,
    )

    val rights = List(
      "a" -> 3,
      "b" -> 4,
    )

    val expectedJoined = List(
      ("a", "a" -> 1, "a" -> 3),
      ("a", "a" -> 10, "a" -> 3),
      ("b", "b" -> 2, "b" -> 4),
    )

    assert(Joins.innerJoin(lefts, rights)(_._1, _._1) === expectedJoined)
  }

  it should "duplicate rows if there is a duplicate right match" in {
    val lefts = List(
      "a" -> 1,
      "b" -> 2,
    )

    val rights = List(
      "a" -> 3,
      "a" -> 30,
      "b" -> 4,
    )

    val expectedJoined = List(
      ("a", "a" -> 1, "a" -> 3),
      ("a", "a" -> 1, "a" -> 30),
      ("b", "b" -> 2, "b" -> 4),
    )

    assert(Joins.innerJoin(lefts, rights)(_._1, _._1) === expectedJoined)
  }

  it should "duplicate rows if there is a duplicate right and left match" in {
    val lefts = List(
      "a" -> 1,
      "a" -> 10,
      "b" -> 2,
    )

    val rights = List(
      "a" -> 3,
      "a" -> 30,
      "b" -> 4,
    )

    val expectedJoined = List(
      ("a", "a" -> 1, "a" -> 3),
      ("a", "a" -> 1, "a" -> 30),
      ("a", "a" -> 10, "a" -> 3),
      ("a", "a" -> 10, "a" -> 30),
      ("b", "b" -> 2, "b" -> 4),
    )

    assert(Joins.innerJoin(lefts, rights)(_._1, _._1) === expectedJoined)
  }

}
