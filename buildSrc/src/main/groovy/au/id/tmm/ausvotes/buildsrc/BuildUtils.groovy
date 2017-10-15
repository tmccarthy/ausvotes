package au.id.tmm.ausvotes.buildsrc

import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files

class BuildUtils {

    static File writeIfMissing(File file, String defaultContent) {
        try {
            Files.createFile(file.toPath())
            Files.write(file.toPath(), defaultContent.getBytes("UTF-8"))
        } catch (FileAlreadyExistsException ignored) {}

        file
    }

    static File createDirectoryIfMissing(File file) {
        try {
            Files.createDirectory(file.toPath())
        } catch (FileAlreadyExistsException ignored) {}

        file
    }
}
