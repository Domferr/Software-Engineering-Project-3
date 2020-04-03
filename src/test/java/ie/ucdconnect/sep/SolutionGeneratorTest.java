package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        staffMembers = StaffMember.fromCSV(readFile(config.getStaffMembersFile().toPath()));
    }

    private static String readFile(Path path) throws IOException {
        return String.join("\n", Files.readAllLines(path));
    }

    private String readSolutionFile(int testSetSize) throws IOException {
        String fileName = "solutionFor"+testSetSize+"Students.csv";
        File solutionFile = new File(config.getTestcaseDirName() + fileName);
        return String.join("\n", Files.readAllLines(solutionFile.toPath()));
    }

    private Map<String, Project> getProjectsMap(int testSetSize) throws IOException {
        File projectFile = new File(config.getTestcaseDirName() + "projectsFor" + testSetSize + "Students.csv");
        List<Project> projects = Project.fromCSV(readFile(projectFile.toPath()), staffMembers);
        return projects.stream().collect(Collectors.toMap(Project::getTitle, Function.identity()));
    }

    private void validateSolution(int testSetSize) throws IOException {
        String fileContent = readSolutionFile(testSetSize);
        Solution.fromCSV(fileContent, staffMembers, getProjectsMap(testSetSize));
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