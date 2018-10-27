package au.id.tmm.ausvotes.api

import java.io.{InputStream, Reader}

import au.id.tmm.http_constants.{HttpHeader, HttpMethod}
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.CharSequenceReader
import unfiltered.netty.ReceivedMessage
import unfiltered.request.HttpRequest

//noinspection NotImplementedCode
final case class MockRequest(
                              path: String,
                              httpMethod: HttpMethod,
                              headers: Map[HttpHeader, List[String]] = Map.empty,
                              body: String = "",
                            ) extends HttpRequest[ReceivedMessage](null) {
  override def inputStream: InputStream = IOUtils.toInputStream(body)

  override def reader: Reader = new CharSequenceReader(body)

  override def protocol: String = ???

  override def method: String = httpMethod.asString

  override def uri: String = path

  override def parameterNames: Iterator[String] = ???

  override def parameterValues(param: String): Seq[String] = ???

  override def headerNames: Iterator[String] = ???

  override def headers(name: String): Iterator[String] = ???

  override def isSecure: Boolean = ???

  override def remoteAddr: String = ???
}
