package ie.ucdconnect.sep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {
    /** Returns the project file based on test set size */
    public static File getProjectFile(Config config, int testSetSize) {
        String fileName = "projectsFor" + testSetSize + "Students.csv";
        return new File(config.getTestcaseDirName() + fileName);
    }

    /** Returns the students file based on test set size */
    public static File getStudentsFile(Config config, int testSetSize) {
        String fileName = "students" + testSetSize + ".csv";
        return new File(config.getTestcaseDirName() + fileName);
    }

    /** Returns the entire content of a file described by the given path. */
    public static String readFile(Path path) throws IOException {
        return String.join("\n", Files.readAllLines(path));
    }
}
