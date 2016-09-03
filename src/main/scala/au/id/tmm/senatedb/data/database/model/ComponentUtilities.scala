package au.id.tmm.senatedb.data.database.model

import au.id.tmm.senatedb.data.BallotId
import au.id.tmm.senatedb.data.database.DriverComponent
import slick.ast.ColumnOption

trait ComponentUtilities { this: DriverComponent =>

  import driver.api._

  val electionIdLength = driver.columnOptions.Length(5, varying = false)
  val groupLength = driver.columnOptions.Length(2, varying = false)
  val candidateIdLength = driver.columnOptions.Length(5, varying = false)
  val ballotIdLength = driver.columnOptions.Length(BallotId.length, varying = false)
  val stateLength = driver.columnOptions.Length(3, varying = false)
  val nameLength = driver.columnOptions.Length(100, varying = true)
  val partyLength = driver.columnOptions.Length(100, varying = true)
  val electorateLength = driver.columnOptions.Length(15, varying = true)

  val groupColumnName = "group"
  val positionInGroupColumnName = "positionInGroup"

  trait CommonColumns { this: Table[_] =>
    def electionIdColumn(extraOptions: ColumnOption[String]*)(implicit tt: slick.ast.TypedType[String]) =
      column[String]("electionId", List(electionIdLength) ++ extraOptions: _*)

    def groupColumn(extraOptions: ColumnOption[String]*)(implicit tt: slick.ast.TypedType[String]) =
      column[String](groupColumnName, List(groupLength) ++ extraOptions: _*)

    def candidateIdColumn(extraOptions: ColumnOption[String]*)(implicit tt: slick.ast.TypedType[String]) =
      column[String]("candidateId", List(candidateIdLength) ++ extraOptions: _*)

    def ballotIdColumn(extraOptions: ColumnOption[String]*)(implicit tt: slick.ast.TypedType[String]) =
      column[String]("ballotId", List(ballotIdLength) ++ extraOptions: _*)

    def stateColumn(extraOptions: ColumnOption[String]*)(implicit tt: slick.ast.TypedType[String]) =
      column[String]("state", List(stateLength) ++ extraOptions: _*)

    def nameColumn(extraOptions: ColumnOption[String]*)(implicit tt: slick.ast.TypedType[String]) =
      column[String]("name", List(nameLength) ++ extraOptions: _*)

    def partyColumn(extraOptions: ColumnOption[String]*)(implicit tt: slick.ast.TypedType[String]) =
      column[String]("party", List(partyLength) ++ extraOptions: _*)

    def electorateColumn(extraOptions: ColumnOption[String]*)(implicit tt: slick.ast.TypedType[String]) =
      column[String]("electorate", List(electorateLength) ++ extraOptions: _*)

    def preferenceColumn(extraOptions: ColumnOption[Option[Int]]*)(implicit tt: slick.ast.TypedType[Option[Int]]) =
      column[Option[Int]]("preference", extraOptions: _*)

    def markColumn(extraOptions: ColumnOption[Option[Char]]*)(implicit tt: slick.ast.TypedType[Option[Char]]) =
      column[Option[Char]]("mark", extraOptions: _*)

    def voteCollectionPointIdColumn(extraOptions: ColumnOption[Int]*)(implicit tt: slick.ast.TypedType[Int]) =
      column[Int]("voteCollectionPointId", extraOptions: _*)

    def positionInGroupColumn(extraOptions: ColumnOption[Int]*)(implicit tt: slick.ast.TypedType[Int]) =
      column[Int](positionInGroupColumnName, extraOptions: _*)

    def countColumn(extraOptions: ColumnOption[Int]*)(implicit tt: slick.ast.TypedType[Int]) =
      column[Int]("count", extraOptions: _*)
  }
}
