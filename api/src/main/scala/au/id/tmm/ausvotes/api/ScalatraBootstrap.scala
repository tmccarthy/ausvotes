package au.id.tmm.ausvotes.api

import javax.servlet.ServletContext

import au.id.tmm.ausvotes.api.controllers.DivisionController
import au.id.tmm.ausvotes.backend
import com.google.inject.Guice
import com.google.inject.name.Names
import net.codingwell.scalaguice.InjectorExtensions._
import org.scalatra.servlet.AsyncSupport
import org.scalatra.{LifeCycle, ScalatraBase}

import scala.concurrent.ExecutionContext

class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext): Unit = {
    val injector = Guice.createInjector(new ApiModule())

    configureContext(context, injector)

    backend.persistence.ManagePersistence.start()

    context.mount(injector.instance[DivisionController], "/division")
  }

  private def configureContext(context: ServletContext, injector: ScalaInjector) = {
    context.setInitParameter(org.scalatra.EnvironmentKey, EnvironmentVars.environmentKey)

    context.setInitParameter(ScalatraBase.HostNameKey, EnvironmentVars.hostName)
    context.setInitParameter(ScalatraBase.PortKey, EnvironmentVars.port.toString)
    context.setInitParameter(ScalatraBase.ForceHttpsKey, EnvironmentVars.forceHttps.toString)

    context.setAttribute(AsyncSupport.ExecutionContextKey, injector.instance[ExecutionContext](Names.named("servletExecutionContext")))
  }

  override def destroy(context: ServletContext): Unit = {
    backend.persistence.ManagePersistence.shutdown()
  }
}