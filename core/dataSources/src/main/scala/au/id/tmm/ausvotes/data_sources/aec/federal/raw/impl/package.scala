package au.id.tmm.ausvotes.data_sources.aec.federal.raw

import fs2.Stream.Compiler
import scalaz.zio.IO

package object impl {

  implicit val zioCompiler: Compiler[IO[Throwable, +?], IO[Throwable, +?]] =
    Compiler.syncInstance[IO[Throwable, +?]](scalaz.zio.interop.catz.taskEffectInstances)

}
