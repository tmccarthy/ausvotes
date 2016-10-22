package au.id.tmm.senatedb.reporting

object ReportAccumulationUtils {

  def combineTallies[K](left: Map[K, Long], right: Map[K, Long]): Map[K, Long] = {
    val keys = left.keySet ++ right.keySet

    keys.toStream
      .map(key => {
        key -> (left.getOrElse[Long](key, 0) + right.getOrElse[Long](key, 0))
      })
      .toMap
  }

  def emptyCountMap[K]: Map[K, Long] = Map.empty.withDefaultValue(0l)

}
