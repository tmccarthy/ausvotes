package au.id.tmm.senatedb.api.services

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestKitBase}
import org.scalactic.source.Position
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, TestSuite}

trait MocksActor extends MockFactory with TestKitBase with BeforeAndAfter with ImplicitSender { this: TestSuite =>

  override implicit lazy val system: akka.actor.ActorSystem = ActorSystem()

  override protected def after(fun: => Any)(implicit pos: Position): Unit = {
    super.after(fun)

    TestKit.shutdownActorSystem(system)
  }

}
