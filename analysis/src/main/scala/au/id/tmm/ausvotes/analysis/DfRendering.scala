package au.id.tmm.ausvotes.analysis

import org.apache.spark.sql.{DataFrame, Encoder}

object DfRendering {

  def asMarkdown(df: DataFrame)(implicit stringEncoder: Encoder[String]): String = {
    val headerRow = df.columns.mkString("|| ", " || ", " ||")
    val body = df.select("*").map(r => Stream.tabulate(r.length)(r.get(_).toString).mkString("| ", " | ", " |")).collect.toList.mkString("\n")

    s"$headerRow\n$body"
  }

}
