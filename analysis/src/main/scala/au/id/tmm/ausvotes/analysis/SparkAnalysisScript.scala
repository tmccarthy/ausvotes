package au.id.tmm.ausvotes.analysis

import org.apache.spark.sql.SparkSession
import scalaz.zio.RTS

abstract class SparkAnalysisScript extends RTS {

  final def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .appName(getClass.getName)
      .config("spark.master", "local")
      .getOrCreate()

    run(spark)
  }

  def run(implicit sparkSession: SparkSession): Unit

}
