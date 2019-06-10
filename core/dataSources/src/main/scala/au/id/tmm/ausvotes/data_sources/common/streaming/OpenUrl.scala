package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}
import java.net.URL

import au.id.tmm.bfect.effects.Sync

object OpenUrl {

  def openUrl[F[+_, +_] : Sync](url: URL): F[IOException, InputStream] =
    syncCatchIOException(url.openStream())

}
