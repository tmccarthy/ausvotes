package au.id.tmm.ausvotes.api

import javax.servlet.ServletContext

import au.id.tmm.ausvotes.api.controllers.DivisionController
import au.id.tmm.ausvotes.backend
import com.google.inject.Guice
import net.codingwell.scalaguice.InjectorExtensions._
import org.scalatra.LifeCycle

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext): Unit = {
    val injector = Guice.createInjector(new ApiModule())

    backend.persistence.ManagePersistence.start()
    // TODO flyway migrate

    context.mount(injector.instance[DivisionController], "/division")
  }

  override def destroy(context: ServletContext): Unit = {
    backend.persistence.ManagePersistence.shutdown()
  }
}