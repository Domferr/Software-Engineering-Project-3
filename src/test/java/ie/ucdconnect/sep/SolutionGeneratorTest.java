package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/** This class unit tests the solution files */
class SolutionGeneratorTest {

    private static Config config;
    private static List<StaffMember> staffMembers;

    @BeforeEach
    void setUp() throws IOException {
        config = Config.getInstance();
        File testCaseDir = new File(config.getTestcaseDirName());
        if (!testCaseDir.exists()) {
            fail(config.getTestcaseDirName() + " does not exist. Please generate test cases");
        }
        if (!testCaseDir.isDirectory()) {
            fail(config.getTestcaseDirName() + " is not a directory");
        }
        staffMembers = StaffMember.fromCSV(Utils.readFile(config.getStaffMembersFile().toPath()));
    }

    private String readSolutionFile(int testSetSize) throws IOException {
        String fileName = "solutionFor"+testSetSize+"Students.csv";
        File solutionFile = new File(config.getTestcaseDirName() + fileName);
        return Utils.readFile(solutionFile.toPath());
    }

    private Map<String, Project> getProjectsMap(int testSetSize) throws IOException {
        File projectFile = Utils.getProjectFile(config, testSetSize);
        List<Project> projects = Project.fromCSV(Utils.readFile(projectFile.toPath()), staffMembers);
        return projects.stream().collect(Collectors.toMap(Project::getTitle, Function.identity()));
    }

    private void validateSolution(int testSetSize) throws IOException {
        String fileContent = readSolutionFile(testSetSize);
        Map<String, Project> projectMap = getProjectsMap(testSetSize);
        File studentsFile = Utils.getStudentsFile(config, testSetSize);
        List<Student> students = Student.fromCSV(Utils.readFile(studentsFile.toPath()), projectMap);
        Solution solution = Solution.fromCSV(fileContent, students, projectMap);
    }

    @Test
    void validateSolution_60Students() {
        assertDoesNotThrow(() -> validateSolution(60));
    }

    @Test
    void validateSolution_120Students() {
        assertDoesNotThrow(() -> validateSolution(120));
    }

    @Test
    void validateSolution_240Students() {
        assertDoesNotThrow(() -> validateSolution(240));
    }

    @Test
    void validateSolution_500Students() {
        assertDoesNotThrow(() -> validateSolution(500));
    }
}