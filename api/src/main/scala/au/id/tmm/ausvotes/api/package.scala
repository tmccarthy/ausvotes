package au.id.tmm.ausvotes

import unfiltered.netty.ReceivedMessage
import unfiltered.request.HttpRequest
import unfiltered.response.ResponseFunction

package object api {

  type Routes[F[+_, +_]] = PartialFunction[HttpRequest[ReceivedMessage], F[Nothing, ResponseFunction[Any]]]

}
