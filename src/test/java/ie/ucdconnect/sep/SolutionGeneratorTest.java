package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class unit tests the solution files
 */
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

	private void validateSolution(int testSetSize) throws IOException {
		//Read the test set from resources
		List<Project> projects = Utils.readProjects(staffMembers, testSetSize);
		Map<String, Project> projectsMap = Utils.generateProjectsMap(projects);
		List<Student> students = Utils.readStudents(testSetSize);

		//Read the solution
		Solution solution = Utils.readSolution(projectsMap, students, testSetSize);

		//Each project should be assigned to one student only
		for (Project project : solution.getProjects()) {
			Collection<Student> assignedStudents = solution.getAssignedStudents(project);
			assertEquals(1, assignedStudents.size());
		}
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