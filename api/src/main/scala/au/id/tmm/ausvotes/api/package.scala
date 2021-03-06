package au.id.tmm.ausvotes

import unfiltered.netty.ReceivedMessage
import unfiltered.request.HttpRequest
import unfiltered.response.ResponseFunction

package object api {

  type PartialRoutes[F[+_, +_]] = PartialFunction[HttpRequest[ReceivedMessage], F[Exception, ResponseFunction[Any]]]
  type CompleteRoutes[F[+_, +_]] = HttpRequest[ReceivedMessage] => F[Exception, ResponseFunction[Any]]
  type InfallibleRoutes[F[+_, +_]] = HttpRequest[ReceivedMessage] => F[Nothing, ResponseFunction[Any]]

}
