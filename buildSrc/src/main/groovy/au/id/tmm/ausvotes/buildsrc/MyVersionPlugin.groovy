package au.id.tmm.ausvotes.buildsrc

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.regex.Pattern

class MyVersionPlugin implements Plugin<Project> {

    private static final Pattern VERSION_TAG_PATTERN = Pattern.compile(/^v(\d.*)$/)

    @Override
    void apply(Project target) {
        assignVersionTo(target)
    }

    private static void assignVersionTo(Project project) {
        def repository = new FileRepositoryBuilder().findGitDir(project.rootDir).build()
        def git = new Git(repository)

        def (isTagged, version) = isTaggedAndVersion(git)
        def workingTreeIsClean = git.status().call().clean
        def isSnapshot = !(workingTreeIsClean && isTagged)

        project.ext.isSnapshot = isSnapshot

        if (isSnapshot) {
            project.version = version + '-SNAPSHOT'
        } else {
            project.version = version
        }
    }

    private static Tuple2<Boolean, String> isTaggedAndVersion(Git git) {
        def versionFromTag = versionFromTag(git)
        def isTagged = versionFromTag != null

        if (isTagged) {
            return new Tuple2(isTagged, versionFromTag)
        } else {
            def versionFromDescribe = git.describe().setMatch("v[0-9]*").call().substring(1)

            return new Tuple2(isTagged, versionFromDescribe)
        }
    }

    private static String versionFromTag(Git git) {
        def tags = git.tagList().call()

        tags.findResult {
            versionFromTag(it.name)
        }
    }

    private static String versionFromTag(String tagName) {
        def groups = VERSION_TAG_PATTERN =~ tagName

        if(groups.hasGroup()) {
            groups[1]
        } else {
            null
        }
    }

}
