package ie.ucdconnect.sep.gui;

import ie.ucdconnect.sep.Project;
import ie.ucdconnect.sep.StaffMember;
import ie.ucdconnect.sep.Student;
import ie.ucdconnect.sep.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/** Class with all the methods needed to load projects, students or solution from a file with a specific extension.
 *  .txt files are read like .csv files. */
public class FileLoader {

    public static List<StaffMember> loadStaffMembersFromCSV(File file) throws IOException {
        String fileContent = Utils.readFile(file.toPath());
        return StaffMember.fromCSV(fileContent);
    }

    public static List<StaffMember> loadStaffMembersFromTXT(File file) throws IOException {
        return loadStaffMembersFromCSV(file);
    }

    public static List<Project> loadProjectsFromCSV(File file, List<StaffMember> staffMembers) throws IOException {
        String fileContent = Utils.readFile(file.toPath());
        return Project.fromCSV(fileContent, staffMembers);
    }

    public static List<Project> loadProjectsFromTXT(File file, List<StaffMember> staffMembers) throws IOException {
        return loadProjectsFromCSV(file, staffMembers);
    }

    public static List<Student> loadStudentsFromCSV(File file, Map<String, Project> projectsMap) throws IOException {
        String fileContent = Utils.readFile(file.toPath());
        return Student.fromCSV(fileContent, projectsMap);
    }

    public static List<Student> loadStudentsFromTXT(File file, Map<String, Project> projectsMap) throws IOException {
        return loadStudentsFromCSV(file, projectsMap);
    }
}