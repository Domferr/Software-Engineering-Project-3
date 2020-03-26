package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.fail;

public class TestCaseTest {

	private static final Pattern STUDENT_TESTCASE_FILENAME_PATTERN = Pattern.compile("students(\\d+)\\.csv");
	private static final Pattern PROJECT_TESTCASE_FILENAME_PATTERN = Pattern.compile("projectsFor(\\d+)Students\\.csv");
	private static final String TESTCASE_DIR = "resources/testcases";

	@Test
	public void testFilenameFormat() {
		File testCaseDir = new File(TESTCASE_DIR);
		if (!testCaseDir.exists()) {
			fail(TESTCASE_DIR + " does not exist. Please generate test cases");
		}
		if (!testCaseDir.isDirectory()) {
			fail(TESTCASE_DIR + " is not a directory");
		}
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
			if (studentMatcher.matches()) {
				int num = Integer.parseInt(studentMatcher.group(1));
				bitMap = bitMap ^ num;
			} else if (projectMatcher.matches()) {
				int num = Integer.parseInt(projectMatcher.group(1));
				bitMap = bitMap ^ num;
			} else {
				fail(fileName + " was not expected");
			}
		}
		if (bitMap != 0) {
			fail("Student and project numbers didn't match!");
		}
	}

	@Test
	public void testTestCaseValidity() {
		File testCaseDir = new File(TESTCASE_DIR);
		for (File testCaseFile : testCaseDir.listFiles()) {
			String fileName = testCaseFile.getName();
			if (STUDENT_TESTCASE_FILENAME_PATTERN.matcher(fileName).matches()) {
				//validateStudentFile(testCaseFile);
			} else if (PROJECT_TESTCASE_FILENAME_PATTERN.matcher(fileName).matches()) {
				//validateProjectFile(testCaseFile);
			}
		}
	}
}
