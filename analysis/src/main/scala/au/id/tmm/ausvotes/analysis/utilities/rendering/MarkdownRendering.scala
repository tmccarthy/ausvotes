package au.id.tmm.ausvotes.analysis.utilities.rendering

object MarkdownRendering {

  def render(headers: Product)(rows: Iterable[Product]): String = {
    val headerRow = headers.productIterator.mkString("|| ", " || ", " ||")
    val body = rows.map(r => r.productIterator.mkString("| ", " | ", " |")).mkString("\n")

    s"$headerRow\n$body"
  }

}
