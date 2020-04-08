package ie.ucdconnect.sep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/** Utility class with some important methods */
public class Utils {

    /** Reads the staff members file */
    public static List<StaffMember> readStaffMembers() throws IOException {
        String fileContent = readFile(Config.getInstance().getStaffMembersFile().toPath());

        return StaffMember.fromCSV(fileContent);
    }

    /** Reads the projects test set file with the given size */
    public static List<Project> readProjects(List<StaffMember> staffMembers, int test_size) throws IOException {
        File projectsFile = getProjectFile(test_size);
        String fileContent = readFile(projectsFile.toPath());

        return Project.fromCSV(fileContent, staffMembers);
    }

    /** Reads the students test set file with the given size */
    public static List<Student> readStudents(Map<String, Project> projectsMap, int test_size) throws IOException {
        File studentsFile = getStudentsFile(test_size);
        String fileContent = readFile(studentsFile.toPath());

        return Student.fromCSV(fileContent, projectsMap);
    }

    /** Returns the project file based on test set size */
    public static File getProjectFile(int testSetSize) throws IOException {
        String fileName = "projectsFor" + testSetSize + "Students.csv";
        return new File(Config.getInstance().getTestcaseDirName() + fileName);
    }

    /** Returns the students file based on test set size */
    public static File getStudentsFile(int testSetSize) throws IOException {
        String fileName = "students" + testSetSize + ".csv";
        return new File(Config.getInstance().getTestcaseDirName() + fileName);
    }

    /** Returns the entire content of a file described by the given path. */
    public static String readFile(Path path) throws IOException {
        return String.join("\n", Files.readAllLines(path));
    }
}
