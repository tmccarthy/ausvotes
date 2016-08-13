package au.id.tmm.senatedb.countengine

import ch.jodersky.jni.nativeLoader

@nativeLoader("countEngine")
private[countengine] object CountEngineInterface {
  @native def count(numCandidates: Int, preferences: Array[Int], ballotIds: Array[Int]): Unit
}
