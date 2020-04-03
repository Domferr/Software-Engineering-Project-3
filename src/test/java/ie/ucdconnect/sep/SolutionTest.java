package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** This class unit tests the solution files */
class SolutionTest {

    private static Config config;
    private Map<Project, Student> projectMapping = new HashMap<>();

    @BeforeEach
    void setUp() throws IOException {
        config = Config.getInstance();
        int size = 60;

        String fileContent = readSolutionFile(size);
        System.out.println(fileContent);
    }

    private String readSolutionFile(int testSetSize) throws IOException {
        String fileName = "solutionFor"+testSetSize+"students.csv";
        File solutionFile = new File(config.getTestcaseDirName() + fileName);
        return String.join("\n", Files.readAllLines(solutionFile.toPath()));
    }

    private boolean validateSolution(Map<Project, Student> solutionMapping) {
        return true;
    }

    @Test
    void validateSolution_60Students() {

    }
}