package au.id.tmm.ausvotes.api

import java.io.OutputStream

import au.id.tmm.http_constants.{HttpHeader, HttpResponseCode}
import unfiltered.response.{HttpResponse, ResponseFunction}

import scala.collection.mutable

final class MockResponse extends HttpResponse[Any](Unit) {

  var responseCode: HttpResponseCode = HttpResponseCode.OK
  var redirect: Option[String] = None
  val headers = new mutable.HashMap[HttpHeader, mutable.Set[String]]() with mutable.MultiMap[HttpHeader, String]

  private val bytes: mutable.ArrayBuffer[Byte] = new mutable.ArrayBuffer[Byte]()

  def content: String = new String(bytes.toArray, "UTF-8")

  override def status(statusCode: Int): Unit = this.responseCode = HttpResponseCode.fromCode(statusCode).get

  override def status: Int = responseCode.code

  override def outputStream: OutputStream = (b: Int) => bytes += b.asInstanceOf[Byte]

  override def redirect(url: String): Unit = redirect = Some(url)

  override def header(name: String, value: String): Unit = headers.addBinding(HttpHeader(name), value)

}

object MockResponse {
  def default: MockResponse = new MockResponse

  def from(responseFunction: ResponseFunction[Any]): MockResponse =
    responseFunction[Any](default).asInstanceOf[MockResponse]
}
