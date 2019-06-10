package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}

import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync.Ops
import au.id.tmm.bfect.effects.extra.Resources

object OpenResource {
  def openResource[F[+_, +_] : Resources](resourceName: String): F[IOException, InputStream] =
    Resources[F].getResourceAsStream(resourceName)
      .flatMap {
        case Some(is) => Sync.pure(is): F[IOException, InputStream]
        case None     => Sync.leftPure(new IOException(s"Resource $resourceName not present")): F[IOException, InputStream]
      }
}
