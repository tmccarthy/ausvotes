package au.id.tmm.ausvotes.analysis.utilities.rendering

object MarkdownRendering {

  def render[
  A1 : MarkdownRenderable,
  A2 : MarkdownRenderable,
  A3 : MarkdownRenderable,
  A4 : MarkdownRenderable,
  A5 : MarkdownRenderable,
  A6 : MarkdownRenderable,
  A7 : MarkdownRenderable,
  A8 : MarkdownRenderable,
  ](
     header1: String,
     header2: String,
     header3: String,
     header4: String,
     header5: String,
     header6: String,
     header7: String,
     header8: String,
   )(rows: Iterable[(A1, A2, A3, A4, A5, A6, A7, A8)]): String =
    unsafeRender(
      List(
        header1,
        header2,
        header3,
        header4,
        header5,
        header6,
        header7,
        header8,
      ),
    )(
      rows.map {
        case (
          a1,
          a2,
          a3,
          a4,
          a5,
          a6,
          a7,
          a8,
          ) => List(
          MarkdownRenderable.render(a1),
          MarkdownRenderable.render(a2),
          MarkdownRenderable.render(a3),
          MarkdownRenderable.render(a4),
          MarkdownRenderable.render(a5),
          MarkdownRenderable.render(a6),
          MarkdownRenderable.render(a7),
          MarkdownRenderable.render(a8),
        )
      }
    )

  def render[
  A1 : MarkdownRenderable,
  A2 : MarkdownRenderable,
  A3 : MarkdownRenderable,
  A4 : MarkdownRenderable,
  A5 : MarkdownRenderable,
  A6 : MarkdownRenderable,
  A7 : MarkdownRenderable,
  ](
     header1: String,
     header2: String,
     header3: String,
     header4: String,
     header5: String,
     header6: String,
     header7: String,
   )(rows: Iterable[(A1, A2, A3, A4, A5, A6, A7)]): String =
    unsafeRender(
      List(
        header1,
        header2,
        header3,
        header4,
        header5,
        header6,
        header7,
      ),
    )(
      rows.map {
        case (
          a1,
          a2,
          a3,
          a4,
          a5,
          a6,
          a7,
          ) => List(
          MarkdownRenderable.render(a1),
          MarkdownRenderable.render(a2),
          MarkdownRenderable.render(a3),
          MarkdownRenderable.render(a4),
          MarkdownRenderable.render(a5),
          MarkdownRenderable.render(a6),
          MarkdownRenderable.render(a7),
        )
      }
    )

  def render[
  A1 : MarkdownRenderable,
  A2 : MarkdownRenderable,
  A3 : MarkdownRenderable,
  A4 : MarkdownRenderable,
  A5 : MarkdownRenderable,
  A6 : MarkdownRenderable,
  ](
     header1: String,
     header2: String,
     header3: String,
     header4: String,
     header5: String,
     header6: String,
   )(rows: Iterable[(A1, A2, A3, A4, A5, A6)]): String =
    unsafeRender(
      List(
        header1,
        header2,
        header3,
        header4,
        header5,
        header6,
      ),
    )(
      rows.map {
        case (
          a1,
          a2,
          a3,
          a4,
          a5,
          a6,
          ) => List(
          MarkdownRenderable.render(a1),
          MarkdownRenderable.render(a2),
          MarkdownRenderable.render(a3),
          MarkdownRenderable.render(a4),
          MarkdownRenderable.render(a5),
          MarkdownRenderable.render(a6),
        )
      }
    )

  def render[
  A1 : MarkdownRenderable,
  A2 : MarkdownRenderable,
  A3 : MarkdownRenderable,
  A4 : MarkdownRenderable,
  A5 : MarkdownRenderable,
  ](
     header1: String,
     header2: String,
     header3: String,
     header4: String,
     header5: String,
   )(rows: Iterable[(A1, A2, A3, A4, A5)]): String =
    unsafeRender(
      List(
        header1,
        header2,
        header3,
        header4,
        header5,
      ),
    )(
      rows.map {
        case (
          a1,
          a2,
          a3,
          a4,
          a5,
          ) => List(
          MarkdownRenderable.render(a1),
          MarkdownRenderable.render(a2),
          MarkdownRenderable.render(a3),
          MarkdownRenderable.render(a4),
          MarkdownRenderable.render(a5),
        )
      }
    )

  def render[
  A1 : MarkdownRenderable,
  A2 : MarkdownRenderable,
  A3 : MarkdownRenderable,
  A4 : MarkdownRenderable,
  ](
     header1: String,
     header2: String,
     header3: String,
     header4: String,
   )(rows: Iterable[(A1, A2, A3, A4)]): String =
    unsafeRender(
      List(
        header1,
        header2,
        header3,
        header4,
      ),
    )(
      rows.map {
        case (
          a1,
          a2,
          a3,
          a4,
          ) => List(
          MarkdownRenderable.render(a1),
          MarkdownRenderable.render(a2),
          MarkdownRenderable.render(a3),
          MarkdownRenderable.render(a4),
        )
      }
    )

  def render[
  A1 : MarkdownRenderable,
  A2 : MarkdownRenderable,
  A3 : MarkdownRenderable,
  ](
     header1: String,
     header2: String,
     header3: String,
   )(rows: Iterable[(A1, A2, A3)]): String =
    unsafeRender(
      List(
        header1,
        header2,
        header3,
      ),
    )(
      rows.map {
        case (
          a1,
          a2,
          a3,
          ) => List(
          MarkdownRenderable.render(a1),
          MarkdownRenderable.render(a2),
          MarkdownRenderable.render(a3),
        )
      }
    )

  def render[
  A1 : MarkdownRenderable,
  A2 : MarkdownRenderable,
  ](
     header1: String,
     header2: String,
   )(rows: Iterable[(A1, A2)]): String =
    unsafeRender(
      List(
        header1,
        header2,
      ),
    )(
      rows.map {
        case (
          a1,
          a2,
          ) => List(
          MarkdownRenderable.render(a1),
          MarkdownRenderable.render(a2),
        )
      }
    )

  def unsafeRender(headers: List[String])(rows: Iterable[List[String]]): String = {

    val widthsPerColumn = rows.foldLeft(headers.map(_.length)) { case (maxLengthsPerRowSoFar, nextRow) =>
      (maxLengthsPerRowSoFar zip nextRow.map(_.length)).map {
        case (maxSoFar, thisCellLength) => math.max(maxSoFar, thisCellLength)
      }
    }

    val table: List[List[String]] = (List(headers) ++ rows).map { row =>
      (row zip widthsPerColumn).map { case (cell, columnWidth) =>
        cell.formatted(s"%-${columnWidth}s")
      }
    }

    table match {
      case headers :: rows => {
        val headerRow = headers.mkString("| ", " | ", " |")
        val headerUnderlineRow = widthsPerColumn.map(width => "-" * width).mkString("| ", " | ", " |")
        val body = rows.map(r => r.mkString("| ", " | ", " |")).mkString("\n")

        s"$headerRow\n$headerUnderlineRow\n$body"
      }
      case Nil => ""
    }
  }

}
