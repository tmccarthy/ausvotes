package au.id.tmm.ausvotes.data_sources.common

import java.net.{MalformedURLException, URL}

object UrlUtils {

  implicit class StringOps(string: String) {
    def parseUrl: Either[MalformedURLException, URL] = try Right(new URL(string)) catch {
      case e: MalformedURLException => Left(e)
    }
  }

}
