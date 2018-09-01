package au.id.tmm.ausvotes.buildsrc.python

import com.linkedin.gradle.python.PythonExtension
import com.linkedin.gradle.python.extension.PythonDetails
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecSpec

class MyPipInstallTask extends DefaultTask {

    @Input
    List<String> packages = []

    @TaskAction
    def doInstall() {
        List<String> commandLine = [
                pythonDetails.virtualEnvInterpreter.toString(),
                pythonDetails.virtualEnvironment.pip,
                'install',
                '--disable-pip-version-check',
                *packages
        ]

        project.exec { ExecSpec execSpec ->
            execSpec.environment(pythonExtension.environment)
            execSpec.commandLine(commandLine)
        }
    }

    PythonDetails getPythonDetails() {
        pythonExtension.details
    }

    PythonExtension getPythonExtension() {
        project.extensions.python
    }
}
