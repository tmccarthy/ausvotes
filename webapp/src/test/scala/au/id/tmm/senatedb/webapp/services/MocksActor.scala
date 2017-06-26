package au.id.tmm.senatedb.webapp.services

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestKitBase}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, TestSuite}

trait MocksActor extends MockFactory with TestKitBase with BeforeAndAfterAll with ImplicitSender { this: TestSuite =>

  override implicit lazy val system: akka.actor.ActorSystem = ActorSystem()

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
}
