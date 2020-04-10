package ie.ucdconnect.sep;

import com.opencsv.CSVParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

public class TestCaseTest {

	private static final Pattern STUDENT_TESTCASE_FILENAME_PATTERN = Pattern.compile("students(\\d+)\\.csv");
	private static final Pattern PROJECT_TESTCASE_FILENAME_PATTERN = Pattern.compile("projectsFor(\\d+)Students\\.csv");
	private static final Pattern SOLUTION_TESTCASE_FILENAME_PATTERN = Pattern.compile("solutionFor(\\d+)Students\\.csv");
	private static final CSVParser CSV_PARSER = new CSVParser();
	private static Config config;

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
	}

	@Test
	public void testFilenameFormat() {
		File testCaseDir = new File(config.getTestcaseDirName());
		// This is a bitmap we will use to ensure all numbers appear exactly twice.
		// We will XOR every number onto this, if every number appears EXACTLY two times, the result will be zero. Otherwise it will be non-zero.
		int bitMap = 0;
		for (File testCaseFile : testCaseDir.listFiles()) {
			String fileName = testCaseFile.getName();
			if (testCaseFile.isDirectory()) {
				fail(fileName + " is a directory");
			}
			Matcher studentMatcher = STUDENT_TESTCASE_FILENAME_PATTERN.matcher(fileName);
			Matcher projectMatcher = PROJECT_TESTCASE_FILENAME_PATTERN.matcher(fileName);
			Matcher solutionMatcher = SOLUTION_TESTCASE_FILENAME_PATTERN.matcher(fileName);
			if (studentMatcher.matches()) {
				int num = Integer.parseInt(studentMatcher.group(1));
				bitMap = bitMap ^ num;
			} else if (projectMatcher.matches()) {
				int num = Integer.parseInt(projectMatcher.group(1));
				bitMap = bitMap ^ num;
			} else if (!solutionMatcher.matches()) {
				fail(fileName + " was not expected");

			}
		}
		if (bitMap != 0) {
			fail("Student and project numbers didn't match!");
		}
	}

	@Test
	public void testTestCaseValidity() throws IOException {
		File testCaseDir = new File(config.getTestcaseDirName());
		// From the above test, we are now assuming students and projects come in same number pairs.
		for (File testCaseFile : testCaseDir.listFiles()) {
			String fileName = testCaseFile.getName();
			Matcher matcher = PROJECT_TESTCASE_FILENAME_PATTERN.matcher(fileName);
			if (matcher.matches()) {
				int num = Integer.parseInt(matcher.group(1));
				validateProjectFile(testCaseFile);
				validateStudentFile(Utils.getStudentsFile(num), testCaseFile);
			}
		}
	}

	private void validateProjectFile(File projectsFile) throws IOException {
		Map<String, String> staffMap = mapStaff();
		for (String line : Files.readAllLines(projectsFile.toPath())) {
			String[] parts = CSV_PARSER.parseLine(line);
			String staffName = parts[0];
			String focus = parts[1];
			String title = parts[2];
			String staffLine = staffMap.get(staffName);
			if (focus.equals("DS")) {
				if (!staffLine.endsWith("Dagon Studies")) {
					fail(title + " proposed by " + staffName + " is not valid (DS project proposed by non DS staff)");
				}
			} else if (staffLine.endsWith("Dagon Studies")) {
				fail(title + " proposed by " + staffName + " is not valid (CS/CSDS project proposed by DS staff)");
			}
		}
	}

	private void validateStudentFile(File studentsFile, File projectsFile) throws IOException {
		// This ensures student IDs are unique. If they are not, an error will be thrown.
		Map<Integer, String> studentsMap = mapStudents(studentsFile);
		Map<String, String> projectsMap = mapProjects(projectsFile);
		for (String line : studentsMap.values()) {
			String[] parts = CSV_PARSER.parseLine(line);
			String studentFocus = parts[4];
			String projects = parts[5];
			for (String projectTitle : projects.split(",")) {
				String projectLine = projectsMap.get(projectTitle);
				String[] projectParts = CSV_PARSER.parseLine(projectLine);
				String projectFocus = projectParts[1];
				if (projectFocus.equals("CS") && !studentFocus.equals("CS") || projectFocus.equals("DS") && !studentFocus.equals("DS")) {
					fail(parts[0] + " focus is " + studentFocus + ". Chose project (" + projectFocus + "): " + projectTitle);
				}
			}
		}
	}

	private Map<String, String> mapProjects(File projectsFile) throws IOException {
		// Maps the projects by their title
		return Files.lines(projectsFile.toPath()).collect(Collectors.toMap(s -> s.substring(s.lastIndexOf(",") + 1), s -> s));
	}

	private Map<String, String> mapStaff() throws IOException {
		// Maps the staff members by their name.
		return Files.lines(Paths.get(config.getStaffMembersFile().getCanonicalPath())).collect(Collectors.toMap(s -> {
			try {
				// We must do this as some staff have an alias: Firstname "alias" lastname
				return CSV_PARSER.parseLine(s)[0];
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}, s -> s));
	}

	private Map<Integer, String> mapStudents(File file) throws IOException {
		// Maps the students by their student number.
		return Files.lines(file.toPath()).collect(Collectors.toMap(s -> Integer.parseInt(s.substring(0, s.indexOf(","))), s -> s));
	}
}
