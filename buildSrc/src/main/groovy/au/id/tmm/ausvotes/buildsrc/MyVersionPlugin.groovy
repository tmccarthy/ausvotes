package au.id.tmm.ausvotes.buildsrc

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project

import static org.eclipse.jgit.lib.Constants.HEAD
import static org.eclipse.jgit.lib.Constants.R_TAGS

class MyVersionPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        assignVersionTo(target)
    }

    private static void assignVersionTo(Project project) {
        def repository = new FileRepositoryBuilder().findGitDir(project.rootDir).build()
        def git = new Git(repository)

        def gitDescription = git.describe().setMatch("v[0-9]*").call()
        def workingTreeIsClean = !git.status().call().uncommittedChanges
        def version = dropPrefix("v", gitDescription)
        def isSnapshot = !(workingTreeIsClean && isHeadTagged(git))

        project.ext.isSnapshot = isSnapshot

        if (isSnapshot) {
            project.version = version + '-SNAPSHOT'
        } else {
            project.version = version
        }
    }

    private static boolean isHeadTagged(Git git) {
        tagsForHead(git).any {
            it.startsWith("v")
        }
    }

    private static Set<String> tagsForHead(Git git) {
        Set<String> tagsForHead = new HashSet()

        def headObjectId = git.repository.resolve(HEAD).toObjectId()

        def allTags = git.tagList().call()

        for (Ref tag : allTags) {
            def tagObjectId = tag.objectId

            if (Objects.equals(tagObjectId, headObjectId)) {
                def tagName = dropPrefix(R_TAGS, tag.name)

                tagsForHead.add(tagName)
            }
        }

        tagsForHead
    }

    private static String dropPrefix(String prefix, String string) {
        if (string.startsWith(prefix)) {
            string.substring(prefix.length())
        } else {
            string
        }
    }
}
