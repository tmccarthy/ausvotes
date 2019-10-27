package au.id.tmm.ausvotes.buildsrc

import com.github.maiflai.ScalaTestPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.api.tasks.scala.ScalaCompile
import org.scoverage.ScoveragePlugin

class MyScalaPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {

        def s = target.ext.s

        def scalaVersion = target.ext.scalaVersion
        def scalaTestVersion = target.ext.scalaTestVersion

        def tmmTestUtilsVersion = target.ext.tmmTestUtilsVersion

        target.plugins.apply(ScalaPlugin.class)
        target.plugins.apply(ScalaTestPlugin.class)
        if (target.findProperty('noScoverage') != true ) {
            applyScoverage(target)
        }

        target.repositories {
            mavenLocal()
            mavenCentral()
        }

        target.configurations {
            scalaCompilerPlugin
        }

        target.dependencies {
            compile "org.scala-lang:scala-library:$scalaVersion"

            testCompile "org.scalatest:scalatest${s}:$scalaTestVersion"
            testCompile "au.id.tmm.tmm-utils:tmm-utils-testing${s}:$tmmTestUtilsVersion"

            // Needed to produce scalatest report
            testRuntime 'org.pegdown:pegdown:1.4.2'

            scalaCompilerPlugin "org.typelevel:kind-projector_2.13.1:0.11.0"
        }

        target.tasks.withType(ScalaCompile) {
            scalaCompileOptions.with {
                additionalParameters = [
                        "-deprecation",                     // Emit warning and location for usages of deprecated APIs.
                        "-encoding", "utf-8",               // Specify character encoding used by source files.
                        "-explaintypes",                    // Explain type errors in more detail.
                        "-feature",                         // Emit warning and location for usages of features that should be imported explicitly.
                        "-language:existentials",           // Existential types (besides wildcard types) can be written and inferred
                        "-language:higherKinds",            // Allow higher-kinded types
                        "-language:implicitConversions",    // Allow implicit conversions
                        "-unchecked",                       // Enable additional warnings where generated code depends on assumptions.
                        "-Xcheckinit",                      // Wrap field accessors to throw an exception on uninitialized access.
                        "-Xfatal-warnings",                 // Fail the compilation if there are any warnings.
                        "-Xlint:constant",                  // Evaluation of a constant arithmetic expression results in an error.
                        "-Xlint:delayedinit-select",        // Selecting member of DelayedInit.
                        "-Xlint:doc-detached",              // A Scaladoc comment appears to be detached from its element.
                        "-Xlint:inaccessible",              // Warn about inaccessible types in method signatures.
//                        "-Xlint:infer-any",                 // Warn when a type argument is inferred to be `Any`. TODO re-enable this
                        "-Xlint:nullary-override",          // Warn when non-nullary `def f()` overrides nullary `def f`.
                        "-Xlint:nullary-unit",              // Warn when nullary methods return Unit.
                        "-Xlint:option-implicit",           // Option.apply used implicit view.
                        "-Xlint:package-object-classes",    // Class or object defined in package object.
                        "-Xlint:poly-implicit-overload",    // Parameterized overloaded implicit methods are not visible as view bounds.
                        "-Xlint:private-shadow",            // A private field (or class parameter) shadows a superclass field.
                        "-Xlint:stars-align",               // Pattern sequence wildcard must align with sequence component.
                        "-Xlint:type-parameter-shadow",     // A local type parameter shadows a type already in scope.
                        "-Ywarn-extra-implicit",            // Warn when more than one implicit parameter section is defined.
                        "-Ywarn-unused:imports",            // Warn if an import selector is not referenced.
                        "-Ywarn-unused:locals",             // Warn if a local definition is unused.
                        "-Ywarn-unused:privates",           // Warn if a private member is unused.
                        "-Ypatmat-exhaust-depth", "80",     // Increase max exhaustion depth

                        "-Xplugin:" + target.configurations.scalaCompilerPlugin.asPath
                ]
            }
        }

        target.tasks.clean {
            delete target.file('out')
        }
    }

    private void applyScoverage(Project target) {
        def s = target.ext.s
        def scoverageVersion = target.ext.scoverageVersion

        target.plugins.apply(ScoveragePlugin.class)

        target.dependencies {
            scoverage "org.scoverage:scalac-scoverage-plugin${s}:$scoverageVersion"
            scoverage "org.scoverage:scalac-scoverage-runtime${s}:$scoverageVersion"
        }

        target.tasks.compileScoverageScala.shouldRunAfter(target.tasks.test)

        target.scoverage {
            coverageOutputCobertura = false
        }

        target.tasks.checkScoverage {
            minimumRate = 0
        }
    }
}
