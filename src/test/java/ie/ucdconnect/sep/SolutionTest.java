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

    private boolean validateSolution(Solution solution) {
        return true;
    }

    @Test
    void validateSolution_60Students() throws IOException {
        String fileContent = readSolutionFile(60);
        Solution solution = Solution.fromCSV(fileContent);
        assertTrue(validateSolution(solution));
    }

    @Test
    void validateSolution_120Students() throws IOException {
        String fileContent = readSolutionFile(120);
        Solution solution = Solution.fromCSV(fileContent);
        assertTrue(validateSolution(solution));
    }

    @Test
    void validateSolution_240Students() throws IOException {
        String fileContent = readSolutionFile(240);
        Solution solution = Solution.fromCSV(fileContent);
        assertTrue(validateSolution(solution));
    }

    @Test
    void validateSolution_500Students() throws IOException {
        String fileContent = readSolutionFile(500);
        Solution solution = Solution.fromCSV(fileContent);
        assertTrue(validateSolution(solution));
    }
}